package net.renarde.dbx.demos.app

import net.renarde.dbx.demos.DemoEvent
import net.renarde.dbx.demos.utils.GenericApp
import net.renarde.dbx.demos.utils.Helpers._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{functions => F}
import scalapb.spark.Implicits._

object ProcessorApp extends GenericApp {
  override val appName = "processor"

  log.info("Starting the generator")
  log.info(s"Generator config: ${appConfig.toJson}")

  val outputTable = TableName(
    db = appConfig.getString("outputs.delta.database"),
    table = appConfig.getString("outputs.delta.table")
  )

  val sourceStream = spark
    .readStream
    .withKafkaOptions(appConfig.getConfig("inputs.kafka"))
    .load()

  val transformedStream = sourceStream
    .select("value")
    .as[Array[Byte]]
    .map(v => DemoEvent.parseFrom(v))
    .toDF()
    .withColumn("event_timestamp", F.col("timestamp").divide(1000).cast("timestamp"))
    .drop("timestamp")

  if (!spark.catalog.databaseExists(outputTable.db)) {
    spark.sql(
      s"""
         |create database ${outputTable.db}
         |location '${appConfig.getString("outputs.delta.databaseLocation")}'"""
        .stripMargin)
  }

  if (!spark.catalog.tableExists(outputTable.db, outputTable.table)) {
    spark.sql(
      s"""
         |CREATE TABLE ${outputTable.db}.${outputTable.table} (${transformedStream.schema.toDDL})
         |USING DELTA
         |""".stripMargin
    )
  }


  val query = transformedStream
    .writeStream
    .format("delta")
    .option("checkpointLocation", appConfig.getString("outputs.checkpointLocation"))
    .trigger(Trigger.ProcessingTime(appConfig.getString("outputs.processingTime")))
    .queryName(appName)
    .toTable(outputTable.toString)


  appConfig.getOption("env") match {
    case Some(value) => if (value != "dev") {
      query.awaitTermination()
    }
  }

  log.info("Processor app gracefully finished")

}
