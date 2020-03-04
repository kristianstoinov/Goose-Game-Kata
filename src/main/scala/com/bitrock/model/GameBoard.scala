package com.bitrock.model

import com.bitrock.config.Config

/**
 * Container for the consituents of the game board, i.e. the squares
 */
object GameBoard {

  val startingSquare = 0
  val victorySquare = Config.Game.victorySquare
  val bridgeSquareFrom = Config.Game.bridgeSquareFrom
  val bridgeSquareTo = Config.Game.bridgeSquareTo
  val gooseSquaresList = Config.Game.gooseSquaresList

  sealed trait Square {
    def index: Int

    def name: String = index.toString
  }

  case object Start extends Square {
    override def index: Int = startingSquare

    override def name: String = "Start"
  }

  case class RegularSquare(index: Int) extends Square

  case class Goose(index: Int) extends Square {
    override def name: String = s"$index, The Goose"
  }

  case object Bridge extends Square {
    override def index: Int = bridgeSquareFrom

    override def name: String = "The Bridge"

    def target: Int = bridgeSquareTo
  }

  case object End extends Square {
    override def index: Int = victorySquare
  }

  object Square {
    def apply(position: Int): Square = {
      position match {
        case GameBoard.startingSquare => Start
        case GameBoard.bridgeSquareFrom => Bridge
        case goose if GameBoard.gooseSquaresList contains (goose) => Goose(position)
        case GameBoard.victorySquare => End
        case _ if position < victorySquare => RegularSquare(position)
        case _ => throw new IllegalArgumentException(s"Cannot have a square beyond $victorySquare ")
      }
    }
  }

}
