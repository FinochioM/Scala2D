package components

import ecs.Component
import graphics.Texture

case class RenderComponent(texture: Texture, width: Float, height: Float) extends Component