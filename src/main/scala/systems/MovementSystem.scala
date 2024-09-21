package systems

import ecs.{System, World}
import components.{PositionComponent, VelocityComponent}

class MovementSystem(world: World) extends System {
  override def update(deltaTime: Float): Unit = {
    val entities = world.componentManager.getEntitiesWithComponents(
      classOf[PositionComponent],
      classOf[VelocityComponent]
    )

    entities.foreach { entity =>
      val maybePosition = world.componentManager.getComponent[PositionComponent](entity)
      val maybeVelocity = world.componentManager.getComponent[VelocityComponent](entity)

      for {
        position <- maybePosition
        velocity <- maybeVelocity
      } {
        val updatedPosition = position.copy(
          x = position.x + velocity.vx * deltaTime,
          y = position.y + velocity.vy * deltaTime
        )
        world.componentManager.addComponent(entity, updatedPosition)
      }
    }
  }
}