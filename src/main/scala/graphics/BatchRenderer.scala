package graphics

import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL31.glDrawElementsInstanced
import org.lwjgl.opengl.GL33.glVertexAttribDivisor

import java.nio.FloatBuffer

class BatchRenderer(maxBatchSize: Int = 1000) {
  private val VERTEX_SIZE = 4 // x, y, u, v
  private val VERTEX_SIZE_BYTES = VERTEX_SIZE * 4
  private val MAX_BATCH_VERTICES = maxBatchSize * 4
  private val MAX_BATCH_INDICES = maxBatchSize * 6

  private var vaoId: Int = 0
  private var vboId: Int = 0
  private var eboId: Int = 0
  private var instanceVBO: Int = 0

  private val vertices = BufferUtils.createFloatBuffer(MAX_BATCH_VERTICES)
  private val instanceData = BufferUtils.createFloatBuffer(maxBatchSize * 16)

  private var spriteCount: Int = 0
  private var indexCount: Int = 0

  init()

  private def init(): Unit = {
    vaoId = glGenVertexArrays()
    glBindVertexArray(vaoId)
    
    vboId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vboId)
    glBufferData(GL_ARRAY_BUFFER, MAX_BATCH_VERTICES * 4, GL_DYNAMIC_DRAW)
    
    glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE_BYTES, 0)
    glEnableVertexAttribArray(0)
    glVertexAttribPointer(1, 2, GL_FLOAT, false, VERTEX_SIZE_BYTES, 2 * 4)
    glEnableVertexAttribArray(1)
    
    instanceVBO = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, instanceVBO)
    glBufferData(GL_ARRAY_BUFFER, maxBatchSize * 16 * 4, GL_DYNAMIC_DRAW)
    
    val vec4Size = 4 * 4
    for (i <- 0 until 4) {
      glVertexAttribPointer(2 + i, 4, GL_FLOAT, false, 16 * 4, i * vec4Size)
      glEnableVertexAttribArray(2 + i)
      glVertexAttribDivisor(2 + i, 1)
    }
    
    val idxBuffer = MemoryUtil.memAllocInt(MAX_BATCH_INDICES)
    var offset = 0
    for (_ <- 0 until maxBatchSize) {
      idxBuffer.put(offset + 0)
      idxBuffer.put(offset + 1)
      idxBuffer.put(offset + 2)
      idxBuffer.put(offset + 2)
      idxBuffer.put(offset + 3)
      idxBuffer.put(offset + 0)
      offset += 4
    }
    idxBuffer.flip()

    eboId = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuffer, GL_STATIC_DRAW)
    MemoryUtil.memFree(idxBuffer)

    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
  }

  def beginBatch(): Unit = {
    vertices.clear()
    instanceData.clear()
    spriteCount = 0
    indexCount = 0
  }

  def drawQuad(x: Float, y: Float, width: Float, height: Float, texCoords: Array[Float], transform: Matrix4f): Unit = {
    if (spriteCount >= maxBatchSize) {
      endBatch()
      beginBatch()
    }
    
    vertices.put(Array(
      x, y, texCoords(0), texCoords(1),
      x + width, y, texCoords(2), texCoords(3),
      x + width, y + height, texCoords(4), texCoords(5),
      x, y + height, texCoords(6), texCoords(7)
    ))
    
    val buffer = BufferUtils.createFloatBuffer(16)
    transform.get(buffer)
    instanceData.put(buffer)

    spriteCount += 1
    indexCount += 6
  }

  def endBatch(): Unit = {
    if (spriteCount == 0) return

    vertices.flip()
    instanceData.flip()
    
    glBindBuffer(GL_ARRAY_BUFFER, vboId)
    glBufferSubData(GL_ARRAY_BUFFER, 0, vertices)
    
    glBindBuffer(GL_ARRAY_BUFFER, instanceVBO)
    glBufferSubData(GL_ARRAY_BUFFER, 0, instanceData)
    
    glBindVertexArray(vaoId)
    glDrawElementsInstanced(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, spriteCount)
    glBindVertexArray(0)

    glBindBuffer(GL_ARRAY_BUFFER, 0)

    vertices.clear()
    instanceData.clear()
    spriteCount = 0
    indexCount = 0
  }

  def cleanup(): Unit = {
    glDeleteBuffers(vboId)
    glDeleteBuffers(eboId)
    glDeleteBuffers(instanceVBO)
    glDeleteVertexArrays(vaoId)
  }
}