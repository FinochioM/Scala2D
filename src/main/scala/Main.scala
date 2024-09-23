import camera.Camera2D
import ecs.World
import systems.{MovementSystem, RenderSystem}
import components.{AtlasSprite, PositionComponent, RenderComponent, SingleTexture, VelocityComponent, ZOrder}
import tools.{ResourceManager, TaskQueue}
import window.Window


object Main {
  def main(args: Array[String]): Unit = {
    val window = new Window(1280, 720, "Scala2D")
    window.init()
    
    val world = new World

    world.addSystem(new MovementSystem(world))
    val renderSystem = new RenderSystem(world, window.width, window.height)
    world.addSystem(renderSystem)
    
    val camera = Camera2D(0f, 0f, window.width.toFloat, window.height.toFloat)
    renderSystem.setCamera(camera)
    
    val atlas = ResourceManager.loadTextureAtlas(
      name = "mainAtlas",
      atlasImagePath = "src/main/resources/textures/Hero_01.png",
      frameWidth = 96,
      frameHeight = 96
    )
    
    atlas.listSprites()

    val entity = world.entityManager.createEntity()
    world.componentManager.addComponent(entity, PositionComponent(50, 50))
    world.componentManager.addComponent(entity, VelocityComponent(100f, 100f))
    world.componentManager.addComponent(entity, ZOrder(order = 2))
    
    val texture = atlas.getSprite("sprite_0_0").get
    world.componentManager.addComponent(entity, RenderComponent(AtlasSprite(texture, atlas), 96f, 96f))
    
    
    val entity2 = world.entityManager.createEntity()
    world.componentManager.addComponent(entity2, PositionComponent(60, 60))
    world.componentManager.addComponent(entity2, ZOrder(order = 3))
    
    val texture2 = ResourceManager.loadTexture("entity2","src/main/resources/textures/test_image2.png")
    world.componentManager.addComponent(entity2, RenderComponent(SingleTexture(texture2), 64f, 64f))
    

    var lastTime = System.nanoTime()
    while (!window.shouldClose()) {
      val currentTime = System.nanoTime()
      val deltaTime = (currentTime - lastTime) / 1e9f
      lastTime = currentTime
      
      TaskQueue.executeAll()

      world.update(deltaTime)
      window.update()
    }
    
    renderSystem.cleanup()
    ResourceManager.cleanup()
    window.cleanup()
  }
}