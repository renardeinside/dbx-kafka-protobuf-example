package net.renarde.dbx

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.SparkSession

package object demos {
  // these are defaults for launching the application in Databricks environment
  // Local variables will be overridden in tests
  implicit val spark: SparkSession = SparkSession.builder().getOrCreate()
  implicit var config: Config = ConfigFactory.load()
}
