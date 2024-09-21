package systems

import ecs.{System, World}
import components.{PositionComponent, RenderComponent}
import graphics.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

class RenderSystem(world: World, windowWidth: Int, windowHeight: Int) extends System {
  private val shaderProgram = new ShaderProgram("shaders/vertex_shader.glsl", "shaders/fragment_shader.glsl")
  private var vaoId: Int = 0
  private var vboId: Int = 0
  private var eboId: Int = 0
  private var modelMatrixLocation: Int = 0
  private var projectionMatrixLocation: Int = 0

  init()

  private def init(): Unit = {
    shaderProgram.use()
    modelMatrixLocation = glGetUniformLocation(shaderProgram.programId, "model")
    if (modelMatrixLocation < 0) {
      throw new RuntimeException("No se pudo encontrar el uniforme 'model' en el shader.")
    }

    projectionMatrixLocation = glGetUniformLocation(shaderProgram.programId, "projection")
    if (projectionMatrixLocation < 0) {
      throw new RuntimeException("No se pudo encontrar el uniforme 'projection' en el shader.")
    }

    val textureUniformLocation = glGetUniformLocation(shaderProgram.programId, "texture1")
    if (textureUniformLocation < 0) {
      throw new RuntimeException("No se pudo encontrar el uniforme 'texture1' en el shader.")
    }
    glUniform1i(textureUniformLocation, 0)

    // Crear la matriz de proyección
    val projectionMatrix = new Matrix4f().ortho2D(0f, windowWidth.toFloat, windowHeight.toFloat, 0f)
    val projectionBuffer = BufferUtils.createFloatBuffer(16)
    projectionMatrix.get(projectionBuffer)
    glUniformMatrix4fv(projectionMatrixLocation, false, projectionBuffer)

    // Definir los vértices (coordenadas normalizadas)
    val vertices = Array(
      // x, y, z, u, v
      0f, 0f, 0.0f, 0.0f, 0.0f, // Inferior izquierda
      1f, 0f, 0.0f, 1.0f, 0.0f, // Inferior derecha
      1f, 1f, 0.0f, 1.0f, 1.0f, // Superior derecha
      0f, 1f, 0.0f, 0.0f, 1.0f  // Superior izquierda
    )

    val indices = Array(
      0, 1, 2,
      2, 3, 0
    )

    vaoId = glGenVertexArrays()
    glBindVertexArray(vaoId)

    vboId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vboId)
    val vertexBuffer = BufferUtils.createFloatBuffer(vertices.length)
    vertexBuffer.put(vertices).flip()
    glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0)
    glEnableVertexAttribArray(0)
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, (3 * 4).toLong)
    glEnableVertexAttribArray(1)

    eboId = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
    val indexBuffer = BufferUtils.createIntBuffer(indices.length)
    indexBuffer.put(indices).flip()
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW)

    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
  }

  override def update(deltaTime: Float): Unit = {
    glClear(GL_COLOR_BUFFER_BIT)
    glClearColor(0f, 0f, 0f, 1.0f)

    shaderProgram.use()

    val entities = world.componentManager.getEntitiesWithComponents(
      classOf[PositionComponent],
      classOf[RenderComponent]
    )

    entities.foreach { entity =>
      val position = world.componentManager.getComponent[PositionComponent](entity).get
      val render = world.componentManager.getComponent[RenderComponent](entity).get

      // Crear la matriz de modelo con escala y traslación
      val modelMatrix = new Matrix4f()
        .translate(position.x, position.y, 0f)
        .scale(render.width, render.height, 1f)

      val modelBuffer = BufferUtils.createFloatBuffer(16)
      modelMatrix.get(modelBuffer)
      glUniformMatrix4fv(modelMatrixLocation, false, modelBuffer)

      glActiveTexture(GL_TEXTURE0)
      render.texture.bind()

      glBindVertexArray(vaoId)
      glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
      glBindVertexArray(0)

      render.texture.unbind()
    }

    glUseProgram(0)
  }

  def cleanup(): Unit = {
    glDisableVertexAttribArray(0)

    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glDeleteBuffers(vboId)

    glBindVertexArray(0)
    glDeleteVertexArrays(vaoId)

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    glDeleteBuffers(eboId)
    shaderProgram.delete()
  }
}
