name := "play-predictionio"

organization := "com.github.filosganga"

version := "1.0-SNAPSHOT"

homepage := Some(url("http://github.com/filosganga/play-predictionio"))

organizationHomepage := Some(url("http://filippodeluca.com"))

licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

startYear := Some(2013)


scmInfo := Some(ScmInfo(
    url("http://github.com/filosganga/play-predictionio"),
    "scm:git:git@github.com:filosganga/play-predictionio.git",
    Some("scm:git:git@github.com:filosganga/play-predictionio.git")
))

scalaVersion := "2.10.2"

crossScalaVersions := Seq("2.10.2")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

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

pomIncludeRepository := { _ => false }

pomExtra := (
  <developers>
    <developer>
      <id>filosganga</id>
      <name>Filippo De Luca</name>
      <url>http://filippodeluca.com</url>
    </developer>
  </developers>)


publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
