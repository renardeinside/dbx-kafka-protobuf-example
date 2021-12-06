package net.renarde.dbx.demos.utils

import org.apache.spark.internal.Logging

trait GenericApp extends App with Logging {
  val appName: String
  lazy val appConfig = ConfigProvider.getGlobalConfig().getConfig(appName).resolve()
}
