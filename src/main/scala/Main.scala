import ecs.World
import systems.{MovementSystem, RenderSystem}
import components.{PositionComponent, RenderComponent, VelocityComponent}
import graphics.Texture
import org.lwjgl.opengl.GL11.glViewport
import window.Window


object Main {
  def main(args: Array[String]): Unit = {
    val window = new Window(1280, 720, "Scala2D")
    window.init()
    
    val world = new World

    world.addSystem(new MovementSystem(world))
    val renderSystem = new RenderSystem(world, window.width, window.height)
    world.addSystem(renderSystem)

    val entity = world.entityManager.createEntity()
    world.componentManager.addComponent(entity, PositionComponent(0f, 0f))
    world.componentManager.addComponent(entity, VelocityComponent(15f, 15f))

    val texture = new Texture("textures/test_image.png")
    world.componentManager.addComponent(entity, RenderComponent(texture, texture.width.toFloat, texture.height.toFloat))
    

    var lastTime = System.nanoTime()
    while (!window.shouldClose()){
      val currentTime = System.nanoTime()
      val deltaTime = (currentTime - lastTime) / 1e9f
      lastTime = currentTime
      
      world.update(deltaTime)
      
      window.update()
    }
    
    renderSystem.cleanup()
    window.cleanup()
  }
}