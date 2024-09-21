package ecs

class EntityManager {
  private var nextId: Long = 0
  private var entities: Set[Entity] = Set()
  
  def createEntity(): Entity = {
    val entity = Entity(nextId)
    nextId += 1
    entities += entity
    entity
  }
  
  def removeEntity(entity: Entity): Unit = {
    entities -= entity
  }
  
  def getAllEntities: Set[Entity] = entities
}