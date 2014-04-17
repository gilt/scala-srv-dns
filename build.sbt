name := "scala-srv-dns"

version := "0.0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "dnsjava" % "dnsjava" % "2.1.6", // http://www.dnsjava.org/
  "org.slf4j" %  "slf4j-api" % "1.7.7",
  "org.specs2" %% "specs2" % "2.3.11" % "test",
  "com.novocode" % "junit-interface" % "0.9" % "test"
)
