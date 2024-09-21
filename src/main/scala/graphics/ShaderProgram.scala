package graphics

import org.lwjgl.opengl.GL11.GL_FALSE
import org.lwjgl.opengl.GL20.*

import scala.io.Source

class ShaderProgram(vertexShaderPath: String, fragmentShaderPath: String){
  val programId: Int = glCreateProgram()
  
  if (programId == 0){
    throw new RuntimeException("No se pudo crear el programa de shaders")
  }
  
  private val vertexShaderId = createShader(vertexShaderPath, GL_VERTEX_SHADER)
  private val fragmentShaderId = createShader(fragmentShaderPath, GL_FRAGMENT_SHADER)
  
  glAttachShader(programId, vertexShaderId)
  glAttachShader(programId, fragmentShaderId)
  
  glLinkProgram(programId)
  if (glGetProgrami(programId, GL_LINK_STATUS) == 0){
    throw new RuntimeException(s"Error al enlazar el programa de shaders: ${glGetProgramInfoLog(programId)}")
  }
  
  glValidateProgram(programId)
  if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0){
    throw new RuntimeException(s"Error al validar el programa de shaders: ${glGetProgramInfoLog(programId)}")
  }
  
  def use(): Unit = glUseProgram(programId)
  
  def delete(): Unit = {
    glUseProgram(0)
    glDetachShader(programId, vertexShaderId)
    glDetachShader(programId, fragmentShaderId)
    glDeleteShader(vertexShaderId)
    glDeleteShader(fragmentShaderId)
    glDeleteProgram(programId)
  }
  
  private def createShader(filePath: String, shaderType: Int): Int = {
    val shaderSource = Source.fromResource(filePath).mkString
    val shaderId = glCreateShader(shaderType)
    glShaderSource(shaderId, shaderSource)
    glCompileShader(shaderId)

    if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
      throw new RuntimeException(s"Error al compilar el shader (${filePath}): ${glGetShaderInfoLog(shaderId)}")
    }
    
    shaderId
  }
}