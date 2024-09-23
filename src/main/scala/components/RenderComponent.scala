package components

import ecs.Component
import graphics.{Sprite, Texture, TextureAtlas}

sealed trait Renderable {
  def texCoords: Array[Float]
  def textureId: Int
}

case class AtlasSprite(sprite: Sprite, atlas: TextureAtlas) extends Renderable {
  override def texCoords: Array[Float] = sprite.texCoords
  override def textureId: Int = atlas.textureId
}

case class SingleTexture(texture: Texture, textCoords: Array[Float] = Array(0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f)) extends Renderable {
  override def texCoords: Array[Float] = textCoords
  override def textureId: Int = texture.textureId
}

case class RenderComponent(renderable: Renderable, width: Float, height: Float) extends Component