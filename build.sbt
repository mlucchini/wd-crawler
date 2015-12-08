import sbtassembly.Plugin._

assemblySettings

name := "crawler"

version := "1.0"

scalaVersion := "2.11.4"

coverageEnabled := true

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.5",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
  "net.ruippeixotog" %% "scala-scraper" % "0.1.1",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "org.slf4j" % "slf4j-nop" % "1.7.12"
)
