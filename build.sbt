name := "scala-srv-dns"

version := "0.0.5"

organization := "com.gilt"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.6", "2.11.8")

useGpg := true

libraryDependencies ++= Seq(
  "dnsjava" % "dnsjava" % "2.1.6", // http://www.dnsjava.org/
  "org.slf4j" %  "slf4j-api" % "1.7.21",
  "org.specs2" %% "specs2" % "2.4.11" % "test" exclude("org.scalaz.stream", "scalaz-stream_2.10") exclude("org.scalaz.stream", "scalaz-stream_2.11") // seems to be a broken transitive dependency at the moment, can't download
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-style" -> url("https://raw.githubusercontent.com/gilt/scala-srv-dns/master/LICENSE"))

homepage := Some(url("https://github.com/gilt/scala-srv-dns"))

pomExtra := (
  <scm>
    <url>https://github.com/gilt/scala-srv-dns.git</url>
    <connection>scm:git:git@github.com:gilt/scala-srv-dns.git</connection>
  </scm>
  <developers>
    <developer>
      <id>andreyk0</id>
      <name>Andrey Kartashov</name>
      <url>https://github.com/andreyk0</url>
    </developer>
  </developers>
)
