import sbtappengine.Plugin.{AppengineKeys => gae}

organization := "com.tsmms.hackathon"

name := "Choices"

version := "0.0.1-SNAPSHOT"

description := "Web-App to support a group choice by observing detailed preferences of all members of the group"

scalaVersion := "2.11.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "org.apache.wicket" % "wicket-core" % "6.19.0",
  "org.wicketstuff" % "wicketstuff-gae-initializer" % "6.19.0"
)

(gae.onStartHooks in gae.devServer in Compile) += { () =>
  println("Server started")
}

(gae.onStopHooks in gae.devServer in Compile) += { () =>
  println("Server stopped")
}