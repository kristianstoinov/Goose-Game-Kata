import sbt._

object Dependencies {
  lazy val parsers = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val configRead = "com.typesafe" % "config" % "1.3.4"
}
