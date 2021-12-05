package net.renarde.dbx.demos

import com.typesafe.config.ConfigFactory
import net.renarde.dbx.demos.utils.Helpers._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.execution.streaming.MemoryStream
import org.scalatest.funsuite.AnyFunSuite

import java.time.Instant

class StreamingAppTest extends AnyFunSuite with Logging with KafkaSupport with SparkSupport {

  val testData: Seq[DemoEvent] = (0 to 5).map(id => {
    DemoEvent(Instant.now().toEpochMilli, id.toString, id * 10)
  })

  def sendToKafka(data: Seq[DemoEvent]): Unit = {
    import spark.implicits._
    implicit val sqlContext: SQLContext = spark.sqlContext
    val byteData = data.map(_.toByteArray)

    val memoryStream = MemoryStream[Array[Byte]]
    memoryStream.addData(byteData)

    val query = memoryStream.toDF()
      .writeStream
      .withKafkaOptions
      .queryName("test-writer-writer")
      .start()


    query.processAllAvailable()
    query.stop()
  }

  test("basic-decoder") {

    config = ConfigFactory.parseString(
      s"""
         |env = "dev"
         |inputs {
         |    "kafka.bootstrap.servers" = "${kafka.bootstrapServers}"
         |    subscribe = "test-topic"
         |    topic = "test-topic"
         |    startingOffsets = "earliest"
         |    processingTime = "10 seconds"
         |}
         |outputs = {
         |  landing_table = "default.kafka_landing"
         |}
         |""".stripMargin
    )

    sendToKafka(testData)
    launchApp()
    assertTests()

  }

  def launchApp(): Unit = {
    val app = StreamingApp
    app.main(Array.empty)
    app.query.processAllAvailable()
    app.query.stop()
  }

  def assertTests(): Unit = {
    import scalapb.spark.Implicits._

    val resultData = spark.read
      .table(config.getString("outputs.landing_table"))

    assert(resultData.count() == testData.length)
  }
}
