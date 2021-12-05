name := "dbx-kafka-protobuf-example"

version := "0.1"

scalaVersion := "2.12.10"

val sparkVersion = "3.1.2"
val testcontainersScalaVersion = "0.39.12"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % sparkVersion
libraryDependencies += "com.thesamet.scalapb" %% "sparksql-scalapb" % "0.11.0"
libraryDependencies += "io.delta" %% "delta-core" % "1.0.0" % "provided"

libraryDependencies += "com.typesafe" % "config" % "1.4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % Test
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-kafka" % testcontainersScalaVersion % Test

// Hadoop contains an old protobuf runtime that is not binary compatible
// with 3.0.0.  We shaded ours to prevent runtime issues.
ThisBuild / assemblyShadeRules := Seq(
  ShadeRule.rename("com.google.protobuf.**" -> "shadeproto.@1").inAll,
  ShadeRule.rename("scala.collection.compat.**" -> "scalacompat.@1").inAll
)

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)