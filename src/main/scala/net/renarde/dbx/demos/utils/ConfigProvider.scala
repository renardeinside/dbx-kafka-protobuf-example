package net.renarde.dbx.demos.utils

import com.typesafe.config.{Config, ConfigFactory}

object ConfigProvider {
  var getGlobalConfig: () => Config = () => ConfigFactory.load()
}
