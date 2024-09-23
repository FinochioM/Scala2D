package systems

import ecs.{System, World}
import components.{PositionComponent, VelocityComponent}
import input.Input
import org.lwjgl.glfw.GLFW._

class MovementSystem(world: World) extends System {
  override def update(deltaTime: Float): Unit = {
    val entities = world.componentManager.getEntitiesWithComponents(
      classOf[PositionComponent],
      classOf[VelocityComponent]
    )

    entities.foreach { entity =>
      val position = world.componentManager.getComponent[PositionComponent](entity).get
      val velocity = world.componentManager.getComponent[VelocityComponent](entity).get

      var dx = 0f
      var dy = 0f

      if (Input.isKeyPressed(GLFW_KEY_W) || Input.isKeyPressed(GLFW_KEY_UP)) {
        dy -= velocity.vy * deltaTime
      }
      if (Input.isKeyPressed(GLFW_KEY_S) || Input.isKeyPressed(GLFW_KEY_DOWN)) {
        dy += velocity.vy * deltaTime
      }
      if (Input.isKeyPressed(GLFW_KEY_A) || Input.isKeyPressed(GLFW_KEY_LEFT)) {
        dx -= velocity.vx * deltaTime
      }
      if (Input.isKeyPressed(GLFW_KEY_D) || Input.isKeyPressed(GLFW_KEY_RIGHT)) {
        dx += velocity.vx * deltaTime
      }

      position.x += dx
      position.y += dy
    }
  }
}