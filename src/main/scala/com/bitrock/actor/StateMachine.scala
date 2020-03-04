package com.bitrock.actor

import com.bitrock.model.GameBoard._
import com.bitrock.model._

import scala.annotation.tailrec

case class StateMachine(sourceState: GameState) {

  def nextState(command: Command): GameState = {

    (sourceState, command) match {
      case (Starting, AddPlayer(name)) => addPlayer(name, Set[Player]())
      case (AddingPlayers(players, _), AddPlayer(name)) => addPlayer(name, players)
      case (AddingPlayers(players, _), MovePlayer(playerName, diceRoll)) => movePlayer(playerName, players, diceRoll)
      case (Playing(players, _), MovePlayer(playerName, diceRoll)) => movePlayer(playerName, players, diceRoll)
      //have to add the prank case
      case (_, Quit) | (Ending(_), _) => Ended()
      case (Error(prev, _), com) => StateMachine(prev).nextState(com)
      case (_, CommandNotUnderstood(input, _)) => Error(sourceState, InputNotUnderstood.apply(input).message)
      case _ => Error(sourceState, IllegalState.message)
    }
  }

  // Computes the new target state after a given player is added
  private def addPlayer(playerName: String, players: Set[Player]): GameState =
    if (players.contains(Player(playerName, Start))) {
      Error(sourceState, s"$playerName: already existing player")
    } else {
      val playersSet = players.+(Player(playerName, Start))

      var playersNames: Set[String] = Set.empty[String]
      playersSet.foreach(player => {
        playersNames = playersNames.+(player.name)
      })
      AddingPlayers(playersSet, s"""players: ${playersNames.mkString(", ")}""")
    }

  // Computes the new target state after a given player moves with a dice roll
  private def movePlayer(playerName: String, players: Set[Player], diceRoll: DiceRoll): GameState = {

    def mapToState(move: Move, message: String): GameState = move.to match {
      case End => Ending(message)
      case square => Playing(players.+(Player(playerName, square)), message)
    }

    // Get the end position of the player and compute the state from it
    val resultingState = for {
      player <- players.filter(player => player.name == playerName).headOption
      moves = move(player.position, diceRoll)
      move <- moves.headOption
    } yield mapToState(move, generateMessage(playerName, moves, diceRoll))

    resultingState.getOrElse(Error(sourceState, PlayerNotFound.apply(playerName).message))
  }

  // Computes the list of moves (transitions from squares) a player makes for a given dice roll
  private def move(sourceSquare: Square, diceRoll: DiceRoll): List[Move] = {
    @tailrec
    def inner(movesList: List[Move]): List[Move] = movesList match {
      case x :: xs => x.to match {
        case b@Bridge => Move(b, Square(b.target)) :: movesList
        case g@Goose(index) => inner(Move(g, Square(index + diceRoll.sum)) :: movesList)
        case RegularSquare(_) | End => movesList
        case Start => throw new IllegalStateException("Cannot go back to Start")
      }
      case _ => movesList
    }

    inner(calculateNextSquares(sourceSquare, diceRoll.sum))
  }


  private def calculateNextSquares(sourceSquare: Square, move: Int): List[Move] = {
    val nextIndex = sourceSquare.index + move
    val endIndex = End.index

    if (nextIndex <= endIndex) {
      Move(sourceSquare, Square(nextIndex), nextIndex == endIndex) :: Nil
    } else {
      val bounceIndex = endIndex - (nextIndex - endIndex)
      Move(End, Square(bounceIndex)) :: Move(sourceSquare, End) :: Nil
    }
  }

  // Returns the text to be output corresponding to a series of moves by a given player
  private def generateMessage(playerName: String, movesList: List[Move], diceRoll: DiceRoll): String = {

    // Returns the message associated with a given game situation (Rule)
    // Would have done this similar to GameRulesViolation if I had more time
    def outputMessage(move: Move): String = (move.from, move.to, move.win) match {
      case (from, End, win) if win => s"$playerName moves from ${from.name} to ${End.name}. $playerName Wins!!"
      case (End, square, _) => s"$playerName bounces! $playerName returns to ${square.name}"
      case (Bridge, to, _) => s"$playerName jumps to ${to.name}"
      case (Goose(_), to, _) => s"$playerName moves again and goes to ${to.name}"
      case (from, to, _) => s"$playerName moves from ${from.name} to ${to.name}"
    }

    val intro = s"$playerName rolls $diceRoll"

    movesList.map(move => outputMessage(move)).foldRight(intro)((m, acc) => s"$acc. $m")
  }

}
