package ecs

trait System{
  def update(deltaTime: Float): Unit
}