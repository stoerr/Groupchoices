import sbtappengine.Plugin.{AppengineKeys => gae}

organization := "com.tsmms.hackathon"

name := "Choices"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.3"

(gae.onStartHooks in gae.devServer in Compile) += { () =>
  println("Server started")
}

(gae.onStopHooks in gae.devServer in Compile) += { () =>
  println("Server stopped")
}