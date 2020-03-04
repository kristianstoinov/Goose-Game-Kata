package com.bitrock

import com.bitrock.actor.StateMachine
import com.bitrock.model.{AddingPlayers, Command, DiceRoll, GameState, Player, Playing}
import com.bitrock.service.CommandParser
import org.scalatest.{FeatureSpec, GivenWhenThen}

abstract class BaseAcceptance extends FeatureSpec with GivenWhenThen {

  val (pippo, pluto) = ("Pippo", "Pluto")

  val roll = DiceRoll(1, 4)

  def init(gameState: GameState) = StateMachine(gameState)

  def add(players: Set[Player]) = AddingPlayers(players, "")

  def playing(ps: Set[Player]) = Playing(ps, "")

  def add(playerName: String) = s"add player $playerName"

  def rollMv(playerName: String, diceRoll: DiceRoll): (DiceRoll, String) = (diceRoll, move(playerName, diceRoll))

  def move(playerName: String, diceRoll: DiceRoll) = s"move $playerName $diceRoll"

  def move(playerName: String) = s"move $playerName"

  // Move with expected roll. Provides the "random" roll to the parser
  def mvWithExpRoll(command: String, expRoll: DiceRoll): Command = CommandParser(() => expRoll).parse(command)

  def userWrites(message: String) = s"""the user writes: "$message" """

  def systemResponds(message: String) = s"""the system responds: "$message""""

}
