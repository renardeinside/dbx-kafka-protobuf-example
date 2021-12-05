package net.renarde.dbx.demos.utils

import com.typesafe.config.{Config, ConfigRenderOptions}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}

import scala.collection.JavaConverters._

object Helpers {

  private val renderOptions = ConfigRenderOptions
    .defaults()
    .setOriginComments(false)
    .setComments(false)
    .setFormatted(true)

  implicit class Formatting(config: Config) {
    def toJson: String = {
      config.root().render(renderOptions)
    }

    def asMap: Map[String, String] = {
      config.root().entrySet().asScala.map(entry => entry.getKey -> entry.getValue.unwrapped().toString).toMap
    }
  }

  implicit class KafkaReaderWithOptions(input: DataStreamReader) {
    def withKafkaOptions(implicit config: Config): DataStreamReader = {
      input.format("kafka").options(config.getConfig("inputs").asMap)
    }
  }

  implicit class KafkaWriterWithOptions[T](input: DataStreamWriter[T]) {
    def withKafkaOptions(implicit config: Config): DataStreamWriter[T] = {
      input.format("kafka").options(config.getConfig("inputs").asMap)
    }
  }
}
