ThisBuild / version := "0.1.0-beta0.1"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "Scala2D"
  )

libraryDependencies ++= Seq(
  "org.lwjgl" % "lwjgl" % "3.3.2",
  "org.lwjgl" % "lwjgl-opengl" % "3.3.2",
  "org.lwjgl" % "lwjgl-glfw" % "3.3.2",
  "org.lwjgl" % "lwjgl-stb" % "3.3.2",
  "org.joml" % "joml" % "1.10.5"
)

libraryDependencies ++= Seq(
  "org.lwjgl" % "lwjgl" % "3.3.2" classifier "natives-windows",
  "org.lwjgl" % "lwjgl-opengl" % "3.3.2" classifier "natives-windows",
  "org.lwjgl" % "lwjgl-glfw" % "3.3.2" classifier "natives-windows",
  "org.lwjgl" % "lwjgl-stb" % "3.3.2" classifier "natives-windows"
)