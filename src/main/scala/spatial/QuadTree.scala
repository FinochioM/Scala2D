package spatial

import components.{PositionComponent, RenderComponent}
import ecs.{Entity, World}
import graphics.{Texture, TextureAtlas}

class QuadTree(var boundary: BoundingBox, capacity: Int = 4) {
  private var entities: List[Entity] = List()
  private var divided: Boolean = false

  var northeast: Option[QuadTree] = None
  var northwest: Option[QuadTree] = None
  var southeast: Option[QuadTree] = None
  var southwest: Option[QuadTree] = None

  def insert(entity: Entity, position: PositionComponent, render: RenderComponent): Boolean = {
    if (!boundary.contains(position.x, position.y, render.width, render.height)) {
      return false
    }

    if (entities.size < capacity) {
      entities = entity :: entities
      true
    } else {
      if (!divided) {
        subdivide()
      }

      if (northeast.exists(_.insert(entity, position, render))) return true
      if (northwest.exists(_.insert(entity, position, render))) return true
      if (southeast.exists(_.insert(entity, position, render))) return true
      if (southwest.exists(_.insert(entity, position, render))) return true

      false
    }
  }

  private def subdivide(): Unit = {
    val x = boundary.x
    val y = boundary.y
    val w = boundary.width / 2
    val h = boundary.height / 2

    northeast = Some(new QuadTree(BoundingBox(x + w, y, w, h), capacity))
    northwest = Some(new QuadTree(BoundingBox(x, y, w, h), capacity))
    southeast = Some(new QuadTree(BoundingBox(x + w, y + h, w, h), capacity))
    southwest = Some(new QuadTree(BoundingBox(x, y + h, w, h), capacity))

    divided = true
  }

  def query(range: BoundingBox, found: List[Entity] = List(), world: World): List[Entity] = {
    var foundEntities = found

    if (!boundary.intersects(range)) {
      return foundEntities
    } else {
      foundEntities ++= entities.filter { entity =>
        val positionOpt = world.componentManager.getComponent[PositionComponent](entity)
        val renderOpt = world.componentManager.getComponent[RenderComponent](entity)
        (positionOpt, renderOpt) match {
          case (Some(pos), Some(render)) => range.contains(pos.x, pos.y, render.width, render.height)
          case _ => false
        }
      }

      if (divided) {
        northeast.foreach(_.query(range, foundEntities, world))
        northwest.foreach(_.query(range, foundEntities, world))
        southeast.foreach(_.query(range, foundEntities, world))
        southwest.foreach(_.query(range, foundEntities, world))
      }

      foundEntities
    }
  }

  def clear(): Unit = {
    entities = List()
    if (divided) {
      northeast.foreach(_.clear())
      northwest.foreach(_.clear())
      southeast.foreach(_.clear())
      southwest.foreach(_.clear())
      northeast = None
      northwest = None
      southeast = None
      southwest = None
      divided = false
    }
  }
}

case class BoundingBox(var x: Float, var y: Float, var width: Float, var height: Float) {
  def contains(entityX: Float, entityY: Float, entityWidth: Float, entityHeight: Float): Boolean = {
    val entityLeft = entityX
    val entityRight = entityX + entityWidth
    val entityBottom = entityY
    val entityTop = entityY + entityHeight

    (entityLeft >= x &&
      entityRight <= x + width &&
      entityBottom >= y &&
      entityTop <= y + height)
  }

  def intersects(other: BoundingBox): Boolean = {
    !(other.x > x + width ||
      other.x + other.width < x ||
      other.y > y + height ||
      other.y + other.height < y)
  }
}
