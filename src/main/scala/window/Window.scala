package window

import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryUtil.NULL

class Window(val width: Int, val height: Int, val title: String) {
  private var windowHandle: Long = 0

  def init(): Unit = {
    if (!glfwInit()) {
      throw new IllegalStateException("No se pudo inicializar GLFW")
    }
    
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    windowHandle = glfwCreateWindow(width, height, title, NULL, NULL)
    if (windowHandle == NULL) {
      throw new RuntimeException("No se pudo crear la ventana")
    }
    
    val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(
      windowHandle,
      (videoMode.width() - width) / 2,
      (videoMode.height() - height) / 2
    )
    
    glfwMakeContextCurrent(windowHandle)
    glfwSwapInterval(1)
    
    glfwShowWindow(windowHandle)
    
    GL.createCapabilities()
    
    val version = glGetString(GL_VERSION)
  }

  def shouldClose(): Boolean = glfwWindowShouldClose(windowHandle)

  def update(): Unit = {
    glfwSwapBuffers(windowHandle)
    glfwPollEvents()
  }

  def cleanup(): Unit = {
    glfwDestroyWindow(windowHandle)
    glfwTerminate()
  }
}