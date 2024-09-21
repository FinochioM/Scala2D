package systems

import ecs.{System, World}
import components.PositionComponent
import events.PositionReachedEvent

class PositionCheckSystem(world: World, targetX: Float, targetY: Float) extends System {
  override def update(deltaTime: Float): Unit = {
    val entities = world.componentManager.getEntitiesWithComponents(classOf[PositionComponent])

    entities.foreach { entity =>
      world.componentManager.getComponent[PositionComponent](entity).foreach { position =>
        if (position.x >= targetX && position.y >= targetY) {
          world.eventManager.publish(PositionReachedEvent(entity))
        }
      }
    }
  }
}