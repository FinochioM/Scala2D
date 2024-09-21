package ecs

import scala.collection.immutable.{Map => ImmutableMap}
import scala.reflect.ClassTag

class ComponentManager {
  private var components: ImmutableMap[Entity, ImmutableMap[Class[_], Component]] = ImmutableMap()

  def addComponent[T <: Component : ClassTag](entity: Entity, component: T): Unit = {
    val entityComponents = components.getOrElse(entity, ImmutableMap())
    val componentClass = implicitly[ClassTag[T]].runtimeClass
    components = components.updated(entity, entityComponents.updated(componentClass, component))
  }

  def removeComponent[T <: Component : ClassTag](entity: Entity): Unit = {
    components.get(entity).foreach { entityComponents =>
      val componentClass = implicitly[ClassTag[T]].runtimeClass
      val updatedComponents = entityComponents - componentClass
      components = components.updated(entity, updatedComponents)
    }
  }

  def getComponent[T <: Component : ClassTag](entity: Entity): Option[T] = {
    components.get(entity).flatMap { entityComponents =>
      val componentClass = implicitly[ClassTag[T]].runtimeClass
      entityComponents.get(componentClass).map(_.asInstanceOf[T])
    }
  }

  def getEntitiesWithComponents(componentClasses: Class[_]*): Set[Entity] = {
    components.collect {
      case (entity, comps) if componentClasses.forall(comps.contains) => entity
    }.toSet
  }
}