name := "play-predictionio"

version := "0.1.0-SNAPSHOT"

organization := "io.prediction"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= {
   val playVersion = "2.2.0"
   Seq(
     "com.typesafe.play" %% "play" % playVersion,
     "com.typesafe.play" %% "play-test" % playVersion,
     "com.typesafe" %% "play-plugins-util" % playVersion,
     "io.prediction" % "client" % "0.6.0",
     "org.specs2" %% "specs2" % "1.14" % "test",
     "org.mockito" % "mockito-all" % "1.9.5" % "test"
   )
}

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))