package graphics

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL30._
import org.lwjgl.stb.STBImage._
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

class Texture(val filePath: String) {
  val textureId: Int = glGenTextures()

  var width: Int = 0
  var height: Int = 0

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

  // Cargar la imagen desde los recursos
  val imageData = {
    val resource = getClass.getClassLoader.getResourceAsStream(filePath)
    if (resource == null) {
      throw new RuntimeException(s"No se pudo encontrar el archivo de imagen: $filePath")
    }
    val imageBytes = resource.readAllBytes()
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
