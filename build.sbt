name := "simpleLoader"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "com.typesafe.play" %% "play-ws" % "2.4.0-M2",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2"
  //"org.scala-lang.modules" %% "scala-async_2.10" % "0.9.2"
)
    