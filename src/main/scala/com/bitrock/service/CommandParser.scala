package com.bitrock.service


import com.bitrock.model._

import scala.util.parsing.combinator.JavaTokenParsers

/**
 * Parser for user input and their translation to commands
 */
final case class CommandParser(rollDice: () => DiceRoll) extends JavaTokenParsers {

  /**
   * Parses the user input and translates it to a Command
   *
   * @param input the command input by the user
   * @return a Command matching the input or CommandNotUnderstood
   */
  def parse(input: String): Command = parseAll(command, input) match {
    case Success(matched, _) => matched
    case Failure(message, _) => CommandNotUnderstood(input, message)
    case Error(message, _) => CommandNotUnderstood(input, message)
  }

  private def diceRoll: Parser[Int] = "[1-6]".r ^^ (_.toInt)

  private def doubleDiceRoll: Parser[DiceRoll] = diceRoll ~ "," ~ diceRoll ^^ {
    case d1 ~ _ ~ d2 => DiceRoll(d1, d2)
  }

  private def addPlayer: Parser[Command] = "add player" ~ ident ^^ {
    case _ ~ name => AddPlayer(name)
  }

  private def movePlayer: Parser[Command] = "move" ~ ident ~ opt(doubleDiceRoll) ^^ {
    case _ ~ name ~ diceRoll => diceRoll match {
      case Some(roll) => MovePlayer(name, roll)
      case None => MovePlayer(name, rollDice())
    }
  }

  private def quit: Parser[Command] = "quit".r ^^ (_ => Quit)

  // Combinator
  private def command: Parser[Command] = addPlayer | movePlayer | quit

}



