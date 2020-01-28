name := "ziohttp"

version := "0.1"

scalaVersion := "2.13.1"

val Http4sVersion = "0.21.0-M6"
val ZIOVersion = "1.0.0-RC17"
val CirceVersion = "0.12.0-M3"

libraryDependencies ++= Seq(
  // ZIO
  "dev.zio" %% "zio" % ZIOVersion,
  "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC10",
  "dev.zio" %% "zio-test" % ZIOVersion % "test",
  "dev.zio" %% "zio-test-sbt" % ZIOVersion % "test",
  "dev.zio" %% "zio-config" % "1.0.0-RC9",
  // Http4s
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  // Circe
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-generic-extras" % CirceVersion,
  // Logback
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")