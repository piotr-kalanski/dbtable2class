name := "dbtable2class"

organization := "com.github.piotr-kalanski"

version := "0.3.2"

scalaVersion := "2.11.8"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/piotr-kalanski/dbtable2class"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/piotr-kalanski/dbtable2class"),
    "scm:git:ssh://github.com/piotr-kalanski/dbtable2class.git"
  )
)

developers := List(
  Developer(
    id    = "kalan",
    name  = "Piotr Kalanski",
    email = "piotr.kalanski@gmail.com",
    url   = url("https://github.com/piotr-kalanski")
  )
)

libraryDependencies ++= Seq(
  "log4j" % "log4j" % "1.2.17",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "junit" % "junit" % "4.10" % "test",
  "com.h2database" % "h2" % "1.4.195" % "test"
)

coverageExcludedPackages := "com.datawizards.dbtable2class.examples.*"

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
