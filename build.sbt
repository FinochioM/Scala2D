ThisBuild / version := "0.1.0-beta0.1"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "Scala2D"
  )

libraryDependencies ++= Seq(
  "org.lwjgl" % "lwjgl" % "3.3.4",
  "org.lwjgl" % "lwjgl-opengl" % "3.3.4",
  "org.lwjgl" % "lwjgl-glfw" % "3.3.4",
  "org.lwjgl" % "lwjgl-stb" % "3.3.4",
  "org.joml" % "joml" % "1.10.7"
)

libraryDependencies ++= Seq(
  "org.lwjgl" % "lwjgl" % "3.3.4" classifier "natives-windows",
  "org.lwjgl" % "lwjgl-opengl" % "3.3.4" classifier "natives-windows",
  "org.lwjgl" % "lwjgl-glfw" % "3.3.4" classifier "natives-windows",
  "org.lwjgl" % "lwjgl-stb" % "3.3.4" classifier "natives-windows"
)