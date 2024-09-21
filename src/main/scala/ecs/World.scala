package ecs

import events.EventManager

class World {
  val entityManager = new EntityManager
  val componentManager = new ComponentManager
  val eventManager = new EventManager
  private var systems: List[System] = List()
  
  def addSystem(system: System): Unit = {
    systems = system :: systems
  }
  
  def update(deltaTime: Float): Unit = {
    systems.foreach(_.update(deltaTime))
  }
}