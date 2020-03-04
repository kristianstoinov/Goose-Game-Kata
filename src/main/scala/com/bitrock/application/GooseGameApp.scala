package com.bitrock.application

import com.bitrock.model.{Command, Ended, GameState, GameStates}

import scala.io.StdIn

object GooseGameApp extends App {

  // Stream of user commands (inputs)
  private lazy val userCommands: Stream[Command] = Stream.continually(StdIn.readLine()).map(in => Command(in))

  private def isEnded(gameState: GameState) = gameState match {
    case e: Ended => true
    case _ => false
  }

  // Initialize the stream and generate the state transitions based on user commands
  new GameStates(userCommands).stream
    .takeWhile(gameState => !isEnded(gameState))
    .map(_.message)
    .foreach(println)
}
