package events

import ecs.Entity

case class PositionReachedEvent(entity: Entity) extends Event
