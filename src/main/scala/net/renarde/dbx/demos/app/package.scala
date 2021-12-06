package net.renarde.dbx.demos

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

package object app {
  // default way to start the session in Databricks environment
  implicit val spark: SparkSession = SparkSession.builder().getOrCreate()
}
