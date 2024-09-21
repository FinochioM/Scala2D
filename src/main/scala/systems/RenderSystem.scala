package systems

import ecs.{System, World}
import components.{PositionComponent, RenderComponent}
import org.lwjgl.opengl.GL11._

class RenderSystem(world: World) extends System {
  override def update(deltaTime: Float): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, 1280, 0, 720, -1, 1)
    glMatrixMode(GL_MODELVIEW)
    
    val entities = world.componentManager.getEntitiesWithComponents(
      classOf[PositionComponent],
      classOf[RenderComponent]
    )
    
    entities.foreach { entity =>
      val maybePosition = world.componentManager.getComponent[PositionComponent](entity)
      maybePosition.foreach { position =>
        glLoadIdentity()
        glTranslatef(position.x * 1280, position.y * 720, 0)

        glBegin(GL_QUADS)
        glColor3f(1f, 0f, 0f)
        glVertex2f(-10f, -10f)
        glVertex2f(10f, -10f)
        glVertex2f(10f, 10f)
        glVertex2f(-10f, 10f)
        glEnd()
      }
    }
  }
}