package net.renarde.dbx.demos.app

import net.renarde.dbx.demos.DemoEvent
import net.renarde.dbx.demos.utils.GenericApp
import net.renarde.dbx.demos.utils.Helpers._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.execution.streaming.MemoryStream

import java.time.Instant

object GeneratorApp extends GenericApp {
  override val appName = "generator"

  log.info("Starting the generator")
  log.info(s"Generator config: ${appConfig.toJson}")

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
      .withKafkaOptions(appConfig.getConfig("outputs.kafka"))
      .queryName(appName)
      .start()

    query.processAllAvailable()
    query.stop()
  }

  sendToKafka(testData)
  log.info("Generator successfully finished")

}
