package com.bitrock.model

import com.bitrock.model.GameBoard.Square

/**
 * A move (transition) from one square to another
 *
 * @param from the square from which the player is moving
 * @param to   the new square towards which the player is moving
 * @param win  indicates if the move is a win
 */
case class Move(from: Square, to: Square, win: Boolean = false)