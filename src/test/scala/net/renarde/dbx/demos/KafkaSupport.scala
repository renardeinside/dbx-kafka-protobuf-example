package net.renarde.dbx.demos

import com.dimafeng.testcontainers.KafkaContainer
import org.apache.spark.internal.Logging
import org.scalatest.{BeforeAndAfterAll, Suite}

trait KafkaSupport extends BeforeAndAfterAll with Logging {
  self: Suite =>

  val kafka = new KafkaContainer()

  override protected def beforeAll(): Unit = {
    kafka.start()
    log.info(s"Kafka is running with bootstrap servers: ${kafka.bootstrapServers}")
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    kafka.stop()
    super.afterAll()
  }
}
