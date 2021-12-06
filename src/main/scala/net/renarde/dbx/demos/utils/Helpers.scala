package net.renarde.dbx.demos.utils

import com.typesafe.config.{Config, ConfigRenderOptions}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}

import scala.collection.JavaConverters._

object Helpers {

  case class TableName(db: String, table: String) {
    override def toString: String = {
      s"${this.db}.${this.table}"
    }
  }

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
    def withKafkaOptions(kafkaConfig: Config): DataStreamReader = {
      input.format("kafka").options(kafkaConfig.asMap)
    }
  }

  implicit class KafkaWriterWithOptions[T](input: DataStreamWriter[T]) {
    def withKafkaOptions(kafkaConfig: Config): DataStreamWriter[T] = {
      input.format("kafka").options(kafkaConfig.asMap)
    }
  }

  implicit class OptionalString(val config: Config) extends AnyVal {
    def getOption(path: String): Option[String] = if (config.hasPath(path)) {
      Some(config.getString(path))
    } else {
      None
    }
  }
}
