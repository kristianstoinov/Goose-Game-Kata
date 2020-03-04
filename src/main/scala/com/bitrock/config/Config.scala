package com.bitrock.config

import com.typesafe.config.ConfigFactory

object Config {

  private val config = ConfigFactory.load("application.conf")

  object Game {
    private val gameConfig = config.getConfig("game")
    private val bridgeConfig = gameConfig.getConfig("bridge")

    val victorySquare = gameConfig.getInt("victory")
    val bridgeSquareFrom = bridgeConfig.getInt("from")
    val bridgeSquareTo = bridgeConfig.getInt("to")
    val gooseSquaresList = gameConfig.getIntList("geese")
  }

}
