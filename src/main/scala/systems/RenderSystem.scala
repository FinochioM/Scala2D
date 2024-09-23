package systems

import ecs.{System, World}
import components.{AtlasSprite, PositionComponent, RenderComponent, SingleTexture, ZOrder}
import graphics.{BatchRenderer, ShaderProgram, Texture, TextureAtlas}
import camera.Camera2D
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL20.{glGetUniformLocation, glUniform1i, glUniformMatrix4fv}
import spatial.{BoundingBox, QuadTree}

class RenderSystem(world: World, windowWidth: Int, windowHeight: Int) extends System {
  private val shaderProgram = new ShaderProgram("shaders/vertex_shader.glsl", "shaders/fragment_shader.glsl")
  private val projectionMatrix = new Matrix4f().ortho2D(0f, windowWidth.toFloat, windowHeight.toFloat, 0f)
  private val batchRenderer = new BatchRenderer()

  private var camera = Camera2D(0f, 0f, windowWidth.toFloat, windowHeight.toFloat)
  private val quadTree = new QuadTree(BoundingBox(camera.x, camera.y, camera.width, camera.height))

  init()

  private def init(): Unit = {
    shaderProgram.use()

    val projectionMatrixLocation = glGetUniformLocation(shaderProgram.programId, "projection")
    val projectionBuffer = BufferUtils.createFloatBuffer(16)
    projectionMatrix.get(projectionBuffer)
    glUniformMatrix4fv(projectionMatrixLocation, false, projectionBuffer)

    val atlasTextureLocation = glGetUniformLocation(shaderProgram.programId, "atlasTexture")
    glUniform1i(atlasTextureLocation, 0)

    val individualTextureLocation = glGetUniformLocation(shaderProgram.programId, "individualTexture")
    glUniform1i(individualTextureLocation, 1)

    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    shaderProgram.use()
  }

  override def update(deltaTime: Float): Unit = {
    val frameStart = System.nanoTime()
    
    glClear(GL_COLOR_BUFFER_BIT)
    glClearColor(0f, 0f, 0f, 1.0f)

    shaderProgram.use()
    
    quadTree.clear()
    
    val entities = world.componentManager.getEntitiesWithComponents(
      classOf[PositionComponent],
      classOf[RenderComponent],
      classOf[ZOrder]
    )

    entities.foreach { entity =>
      val positionOpt = world.componentManager.getComponent[PositionComponent](entity)
      val renderOpt = world.componentManager.getComponent[RenderComponent](entity)
      (positionOpt, renderOpt) match {
        case (Some(position), Some(render)) =>
          quadTree.insert(entity, position, render)
        case _ =>
      }
    }
    
    val frustum = BoundingBox(camera.x, camera.y, camera.width, camera.height)
    
    val visibleEntities = quadTree.query(frustum, List(), world)
    
    val sortedEntities = visibleEntities.toList.sortBy { entity =>
      world.componentManager.getComponent[ZOrder](entity).map(_.order).getOrElse(0)
    }
    
    var currentTextureType: Option[String] = None
    var currentTextureId: Option[Int] = None
    var batchStarted = false

    sortedEntities.foreach { entity =>
      val positionOpt = world.componentManager.getComponent[PositionComponent](entity)
      val renderOpt = world.componentManager.getComponent[RenderComponent](entity)

      (positionOpt, renderOpt) match {
        case (Some(position), Some(render)) =>
          val (textureType, textureId, texCoords, transform) = render.renderable match {
            case AtlasSprite(sprite, atlas) =>
              ("atlas", atlas.textureId, sprite.texCoords, new Matrix4f().translate(position.x, position.y, 0f))
            case SingleTexture(texture, coords) =>
              ("single", texture.textureId, coords, new Matrix4f().translate(position.x, position.y, 0f))
          }
          
          if (currentTextureType.contains(textureType) && currentTextureId.contains(textureId)) {
            batchRenderer.drawQuad(0f, 0f, render.width, render.height, texCoords, transform)
          } else {
            if (batchStarted) {
              batchRenderer.endBatch()
            }
            
            batchRenderer.beginBatch()
            
            textureType match {
              case "atlas" =>
                glActiveTexture(GL_TEXTURE0)
                glBindTexture(GL_TEXTURE_2D, textureId)
                glUniform1i(glGetUniformLocation(shaderProgram.programId, "useAtlas"), 1)
              case "single" =>
                glActiveTexture(GL_TEXTURE1)
                glBindTexture(GL_TEXTURE_2D, textureId)
                glUniform1i(glGetUniformLocation(shaderProgram.programId, "useAtlas"), 0)
            }
            
            currentTextureType = Some(textureType)
            currentTextureId = Some(textureId)
            batchStarted = true
            
            batchRenderer.drawQuad(0f, 0f, render.width, render.height, texCoords, transform)
          }

        case _ =>
      }
    }
    
    if (batchStarted) {
      batchRenderer.endBatch()
    }
    
    val frameEnd = System.nanoTime()
    val frameTime = (frameEnd - frameStart) / 1e6f // en ms
  }

  def cleanup(): Unit = {
    batchRenderer.cleanup()
    shaderProgram.delete()
  }

  def setCamera(newCamera: Camera2D): Unit = {
    camera = newCamera
    quadTree.clear()
    quadTree.boundary.x = newCamera.x
    quadTree.boundary.y = newCamera.y
    quadTree.boundary.width = newCamera.width
    quadTree.boundary.height = newCamera.height
  }
}