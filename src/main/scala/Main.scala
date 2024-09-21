import ecs.World
import systems.{MovementSystem, RenderSystem}
import components.{PositionComponent, RenderComponent, VelocityComponent}
import window.Window

object Main {
  def main(args: Array[String]): Unit = {
    val window = new Window(1280, 720, "Scala2D")
    window.init()
    
    val world = new World

    world.addSystem(new MovementSystem(world))
    world.addSystem(new RenderSystem(world))

    val entity = world.entityManager.createEntity()
    world.componentManager.addComponent(entity, PositionComponent(0f, 0f))
    world.componentManager.addComponent(entity, VelocityComponent(0.1f, 0.1f))
    world.componentManager.addComponent(entity, RenderComponent())
    

    var lastTime = System.nanoTime()
    while (!window.shouldClose()){
      val currentTime = System.nanoTime()
      val deltaTime = (currentTime - lastTime) / 1e9f
      lastTime = currentTime
      
      world.update(deltaTime)
      
      window.update()
    }
    
    window.cleanup()
  }
}