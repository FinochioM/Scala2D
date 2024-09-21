package systems

import ecs.{System, World}
import components.{PositionComponent, RenderComponent}
import graphics.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

class RenderSystem(world: World) extends System {
  private val shaderProgram = new ShaderProgram("shaders/vertex_shader.glsl", "shaders/fragment_shader.glsl")
  private var vaoId: Int = 0
  private var vboId: Int = 0
  private var modelMatrixLocation: Int = 0
  
  init()
  
  private def init(): Unit = {
    shaderProgram.use()
    modelMatrixLocation
    
    val vertices = Array(
      -0.05f, -0.05f, 0.0f,
      0.05f, -0.05f, 0.0f,
      0.0f,   0.05f, 0.0f
    )

    vaoId = glGenVertexArrays()
    glBindVertexArray(vaoId)
    
    vboId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vboId)
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(0)
    
    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
  }

  override def update(deltaTime: Float): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glClearColor(0f, 0f, 0f, 1.0f)
    
    shaderProgram.use()

    val entities = world.componentManager.getEntitiesWithComponents(
      classOf[PositionComponent],
      classOf[RenderComponent]
    )

    entities.foreach { entity =>
      val maybePosition = world.componentManager.getComponent[PositionComponent](entity)
      maybePosition.foreach { position =>
        val modelMatrix = new Matrix4f().translate(position.x, position.y, 0f)
        
        val modelBuffer = BufferUtils.createFloatBuffer(16)
        modelMatrix.get(modelBuffer)
        glUniformMatrix4fv(modelMatrixLocation, false, modelBuffer)
        
        glBindVertexArray(vaoId)
        glDrawArrays(GL_TRIANGLES, 0, 3)
        glBindVertexArray(0)
      }
    }
    
    glUseProgram(0)
  }

  def cleanup(): Unit = {
    glDisableVertexAttribArray(0)
    
    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glDeleteBuffers(vboId)
    
    glBindVertexArray(0)
    glDeleteVertexArrays(vaoId)
    
    shaderProgram.delete()
  }
}