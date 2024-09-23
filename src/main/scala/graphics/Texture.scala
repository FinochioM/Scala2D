package graphics


import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL30._
import org.lwjgl.stb.STBImage._
import org.lwjgl.system.{MemoryStack, MemoryUtil}

import java.nio.file.{Files, Paths}

class Texture(val filePath: String) {
  val textureId: Int = glGenTextures()

  var width: Int = 0
  var height: Int = 0

  private def loadTexture(): Unit = {
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

    val imageData = {
      val imageBytes = Files.readAllBytes(Paths.get(filePath))
      val imageBuffer = MemoryUtil.memAlloc(imageBytes.length)
      imageBuffer.put(imageBytes).flip()

      val loadedImageData = stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelsBuffer, 4)
      MemoryUtil.memFree(imageBuffer)

      if (loadedImageData == null) {
        throw new RuntimeException("No se pudo cargar la imagen: " + stbi_failure_reason())
      }

      width = widthBuffer.get(0)
      height = heightBuffer.get(0)
      loadedImageData
    }

    glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData)

    stbi_image_free(imageData)
    stack.pop()

    glBindTexture(GL_TEXTURE_2D, 0)
  }

  loadTexture()

  def reload(): Unit = {
    loadTexture()
  }

  def bind(): Unit = {
    glBindTexture(GL_TEXTURE_2D, textureId)
  }

  def unbind(): Unit = {
    glBindTexture(GL_TEXTURE_2D, 0)
  }

  def cleanup(): Unit = {
    glDeleteTextures(textureId)
  }
}