package tools

import graphics.{Texture, TextureAtlas}
import scala.collection.mutable

object ResourceManager {
  private val textures = mutable.Map[String, Texture]()
  private val atlases = mutable.Map[String, TextureAtlas]()

  // Cargar una textura individual
  def loadTexture(name: String, filePath: String): Texture = {
    textures.getOrElseUpdate(name, new Texture(filePath))
  }

  def getTexture(name: String): Option[Texture] = textures.get(name)

  // Cargar un atlas de texturas
  def loadTextureAtlas(name: String, atlasImagePath: String, frameWidth: Int, frameHeight: Int): TextureAtlas = {
    atlases.getOrElseUpdate(name, new TextureAtlas(atlasImagePath, frameWidth, frameHeight))
  }

  def getTextureAtlas(name: String): Option[TextureAtlas] = atlases.get(name)

  def cleanup(): Unit = {
    textures.values.foreach(_.cleanup())
    atlases.values.foreach(_.cleanup())
    textures.clear()
    atlases.clear()
  }
}