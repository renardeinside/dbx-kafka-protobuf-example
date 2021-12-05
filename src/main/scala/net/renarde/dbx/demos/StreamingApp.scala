package net.renarde.dbx.demos

import net.renarde.dbx.demos.utils.Helpers._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{functions => F}


object StreamingApp extends App with Logging {

  log.info("Starting the streaming application")
  log.info(s"Application configuration: ${config.toJson}")


  val sourceStream = spark
    .readStream
    .withKafkaOptions
    .load()

  def reader(batch: DataFrame, batchId: Long): Unit = {

    import scalapb.spark.Implicits._

    batch
      .select( "value")
      .as[Array[Byte]]
      .map(v => DemoEvent.parseFrom(v))
      .toDF()
      .withColumn("event_timestamp", F.col("timestamp").divide(1000).cast("timestamp"))
      .drop("timestamp")
      .write
      .format("delta")
      .saveAsTable(config.getString("outputs.landing_table"))
  }

  val query = sourceStream
    .select("timestamp", "value")
    .writeStream
    .foreachBatch(reader _)
    .trigger(Trigger.ProcessingTime(config.getConfig("inputs").getString("processingTime")))
    .start()

  if (config.getString("env") != "dev") {
    query.awaitTermination()
  }

  log.info("Streaming application gracefully finished")


}