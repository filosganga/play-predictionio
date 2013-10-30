name := "play-predictionio"

organization := "io.prediction"

version := "0.1.0-SNAPSHOT"

licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

scalaVersion := "2.10.2"

crossScalaVersions := Seq("2.10.2")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies ++= {
   val playVersion = "2.2.0"
   Seq(
     "io.prediction" % "client" % "0.6.1",
     "com.typesafe.play" %% "play" % playVersion % "provided",
     "org.specs2" %% "specs2" % "1.14" % "test",
     "org.mockito" % "mockito-all" % "1.9.5" % "test",
     "com.typesafe.play" %% "play-test" % playVersion % "test"
   )
}

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))