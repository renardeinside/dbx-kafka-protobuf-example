package net.renarde.dbx.demos.app

import com.typesafe.config.{Config, ConfigFactory}
import net.renarde.dbx.demos.utils.ConfigProvider
import net.renarde.dbx.demos.{KafkaSupport, SparkSupport}
import org.apache.spark.internal.Logging
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.Files

class UnifiedAppTest extends AnyFunSuite with Logging with KafkaSupport with SparkSupport {
  // this test verifies both Generator and Processor apps inside a single test
  val testFileLocation: String = Files.createTempDirectory("").toString

  def getTestConfig: Config = {
    val baseConfig = ConfigFactory.parseString(
      s"""
         |KAFKA_BOOTSTRAP_SERVERS = "${kafka.bootstrapServers}"
         |common.checkpointsLocation = "${testFileLocation}/checkpoints"
         |common.databaseLocation = "${testFileLocation}/db"
         |""".stripMargin)

    val app = ConfigFactory.parseResources("application.conf")
    baseConfig.withFallback(app).resolve()
  }

  test("generator") {
    ConfigProvider.getGlobalConfig = () => getTestConfig

    val generator = GeneratorApp
    generator.main(Array.empty)

    val processor = ProcessorApp
    processor.main(Array.empty)

    assert(
      spark.table(processor.outputTable.toString).count() === generator.testData.length
    )

  }

}
