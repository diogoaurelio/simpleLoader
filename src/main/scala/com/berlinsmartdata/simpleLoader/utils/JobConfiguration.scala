package com.berlinsmartdata.simpleLoader.utils

import com.typesafe.config.{Config, ConfigFactory}

object JobConfiguration {

  val DEFAULT_CONF = "application_prod.conf"

  def getConfiguration(confFile: String = DEFAULT_CONF): Config ={
    ConfigFactory.load(confFile)

  }
}