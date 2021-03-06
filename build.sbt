name := "scala-srv-dns"

version := "0.0.7"

organization := "com.gilt"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.6")

useGpg := true

val specsVersion = "3.8.6"

libraryDependencies ++= Seq(
  "dnsjava" % "dnsjava" % "2.1.6", // http://www.dnsjava.org/
  "org.slf4j" %  "slf4j-api" % "1.7.25",
  "org.specs2" %% "specs2-core" % specsVersion  % "test",
  "org.specs2" %% "specs2-junit" % specsVersion  % "test"
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
