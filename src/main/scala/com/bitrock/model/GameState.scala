package com.bitrock.model

import com.bitrock.actor.StateMachine

sealed trait GameState {
  def message: String
}

// The initial state of the game, before players are added
case object Starting extends GameState {
  override def message: String =
    """Type "add player <name>" to add a player.
      |Type "move <name> 3, 4" to move the player with a specific dice roll.
      |Type "move <name> if you want the system to move the player.
      |Type "quit" at any moment to quit the game.
      |Have Fun!""".stripMargin
}

/**
 * The state where players can be added
 *
 * @param players the set of players
 * @param message the message to be output when entering this state
 */
case class AddingPlayers(players: Set[Player], message: String) extends GameState

/**
 * The main state of the game, where players can be moved.
 *
 * @param players the set of players
 * @param message the message to be output when entering this state
 */
case class Playing(players: Set[Player], message: String) extends GameState

case class Ending(message: String) extends GameState

case class Ended(message: String = "Bye!") extends GameState

/**
 * A state that represents an error from either a bad input or an illegal transition.
 *
 * @param previous a reference to the last non error known state
 * @param message  the error message to be displayed when entering this state
 */
case class Error(previous: GameState, message: String) extends GameState

/**
 * Generates a (potentially infinite) lazily evaluated stream of states transitions.
 * The transitions occur each time a new command comes in on the command stream.
 * The latter is passed by name to its head being evaluated before outputing the Starting stream
 *
 * @param streamOfUserCommands the stream of user commands (inputs)
 */
class GameStates(streamOfUserCommands: => Stream[Command]) {

  val stream: Stream[GameState] = Starting #:: streamOfUserCommands.zip(stream).map {
    case (command, gameState) => nextState(gameState, command)
  }

  private def nextState(gameState: GameState, command: Command): GameState = StateMachine(gameState).nextState(command)

}
