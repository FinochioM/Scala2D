package input

import org.lwjgl.glfw.GLFW._

object Input {
  private val keys = new Array[Boolean](GLFW_KEY_LAST)

  def handleKeyInput(key: Int, action: Int): Unit = {
    if (key >= 0 && key < keys.length) {
      keys(key) = action != GLFW_RELEASE
    }
  }

  def isKeyPressed(key: Int): Boolean = {
    if (key >= 0 && key < keys.length) {
      keys(key)
    } else {
      false
    }
  }
}
