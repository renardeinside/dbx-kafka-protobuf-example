package net.renarde.dbx.demos

import org.apache.spark.sql.SparkSession
import java.nio.file.Files

trait SparkSupport {

  implicit val spark: SparkSession = SparkSession
    .builder()
    .appName("dbx-test")
    .master("local[*]")
    .config("spark.sql.streaming.checkpointLocation", Files.createTempDirectory("tests").toString)
    .config("spark.sql.warehouse.dir", Files.createTempDirectory("warehouse").toString)
    .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
    .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
    .getOrCreate()

}
