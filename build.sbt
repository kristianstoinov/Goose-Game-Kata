import Dependencies._

name := "Goose Game Kata"
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.bitrock",
      scalaVersion := "2.12.7",
      version := "0.1.0"
    )),
    name := "gioco-delloca",
    libraryDependencies ++= {
      Seq(
        parsers,
        configRead,
        scalaTest % Test
      )
    }
  )
