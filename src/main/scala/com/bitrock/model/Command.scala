package com.bitrock.model

import com.bitrock.service.CommandParser

import scala.util.Random

// Commands that represent parsed user input
sealed trait Command

case class MovePlayer(playerName: String, diceRoll: DiceRoll) extends Command

case class AddPlayer(playerName: String) extends Command

case class CommandNotUnderstood(in: String, err: String) extends Command

case object Quit extends Command

object Command {
  // Rolls a dice for the case where none is provided
  private def rand6: Int = Random.nextInt(6) + 1

  private def rollDice() = DiceRoll(rand6, rand6)

  def apply(in: String): Command = CommandParser(() => rollDice()).parse(in)
}