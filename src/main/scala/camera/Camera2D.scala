package camera

case class Camera2D(x: Float, y: Float, width: Float, height: Float) {
  def isVisible(entityX: Float, entityY: Float, entityWidth: Float, entityHeight: Float): Boolean = {
    val cameraLeft = x
    val cameraRight = x + width
    val cameraBottom = y
    val cameraTop = y + height

    val entityLeft = entityX
    val entityRight = entityX + entityWidth
    val entityBottom = entityY
    val entityTop = entityY + entityHeight

    !(entityRight < cameraLeft ||
      entityLeft > cameraRight ||
      entityTop < cameraBottom ||
      entityBottom > cameraTop)
  }
}