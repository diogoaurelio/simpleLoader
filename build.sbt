lazy val scalaMajorVersion = "2.11"
lazy val commonSettings = Seq(
  organization := "com.berlinsmartdata",
  version := "0.1.0",
  scalaVersion := scalaMajorVersion + ".7" //"2.10.6"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "simpleLoader"
  )

lazy val experimental = "-experimental"
lazy val akkaVersion =  "2.4.3" // "2.4.11" // "2.4.2" // "2.0.4"

lazy val akkaGroupId = "com.typesafe.akka"

// I played with different versions of akka, and in some of them you need to add "experimental" to the package name
lazy val akkaActor = "akka-actor" +"_"+scalaMajorVersion
lazy val akkaStream = "akka-stream" +"_"+scalaMajorVersion
lazy val akkaHttp = "akka-http" +experimental +"_"+scalaMajorVersion
lazy val akkaJson = "akka-http-spray-json" +experimental +"_"+scalaMajorVersion
lazy val akkaHttpTest = "akka-http-testkit"


lazy val sprayGroupId = "io.spray"
lazy val sprayVersion = "1.3.2"
lazy val sprayJson = "spray-json" +"_"+scalaMajorVersion
lazy val sprayRouting = "spray-routing" +"_"+scalaMajorVersion



resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0"
  , "com.typesafe.play" %% "play-ws" % "2.4.0-M2"
  , "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
  , "org.scala-lang.modules" %% "scala-async" % "0.9.2"
  , akkaGroupId % akkaActor % akkaVersion
  , akkaGroupId % akkaHttp % akkaVersion
  , akkaGroupId % akkaStream % akkaVersion
  , akkaGroupId % akkaJson % akkaVersion
  , akkaGroupId %% akkaHttpTest % akkaVersion
  //, sprayGroupId % sprayJson  % sprayVersion
  //, sprayGroupId % sprayRouting % sprayVersion

)
    