name := "dbx-kafka-protobuf-example"

version := "0.1"

scalaVersion := "2.12.10"

val sparkVersion = "3.1.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test