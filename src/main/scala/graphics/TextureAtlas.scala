package graphics

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.stb.STBImage._
import org.lwjgl.system.MemoryStack

import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

case class Sprite(name: String, texCoords: Array[Float])

class TextureAtlas(atlasImagePath: String, frameWidth: Int, frameHeight: Int) {
  val textureId: Int = glGenTextures()
  var width: Int = 0
  var height: Int = 0
  private val sprites = mutable.Map[String, Sprite]()

  loadTexture(atlasImagePath)
  generateSprites(frameWidth, frameHeight)

  private def loadTexture(filePath: String): Unit = {
    glBindTexture(GL_TEXTURE_2D, textureId)

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

    val stack = MemoryStack.stackPush()
    val widthBuffer = stack.mallocInt(1)
    val heightBuffer = stack.mallocInt(1)
    val channelsBuffer = stack.mallocInt(1)

    stbi_set_flip_vertically_on_load(false)

    val imageData = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4)
    if (imageData == null) {
      throw new RuntimeException(s"Failed to load texture file: $filePath")
    }

    width = widthBuffer.get(0)
    height = heightBuffer.get(0)

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData)

    stbi_image_free(imageData)
    stack.pop()

    glBindTexture(GL_TEXTURE_2D, 0)
  }

  private def generateSprites(frameWidth: Int, frameHeight: Int): Unit = {
    val cols = width / frameWidth
    val rows = height / frameHeight

    for (row <- 0 until rows) {
      for (col <- 0 until cols) {
        val x = col * frameWidth
        val y = row * frameHeight
        val name = s"sprite_${row}_$col"
        val texCoords = calculateTexCoords(x, y, frameWidth, frameHeight)
        sprites += (name -> Sprite(name, texCoords))
      }
    }
  }

  private def calculateTexCoords(x: Int, y: Int, w: Int, h: Int): Array[Float] = {
    val u0 = x.toFloat / width
    val v0 = y.toFloat / height
    val u1 = (x + w).toFloat / width
    val v1 = (y + h).toFloat / height

    Array(
      u0, v0,
      u1, v0,
      u1, v1,
      u0, v1
    )
  }

  def getSprite(name: String): Option[Sprite] = sprites.get(name)

  def bind(): Unit = {
    glBindTexture(GL_TEXTURE_2D, textureId)
  }

  def unbind(): Unit = {
    glBindTexture(GL_TEXTURE_2D, 0)
  }

  def cleanup(): Unit = {
    glDeleteTextures(textureId)
  }

  def listSprites(): Unit = {
    sprites.keys.foreach(println)
  }
}