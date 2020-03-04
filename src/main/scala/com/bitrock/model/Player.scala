package com.bitrock.model

import com.bitrock.model.GameBoard.Square

/**
 * The state of the player on the board.
 *
 * @param name     the player's name
 * @param position the player's current position
 */
case class Player(name: String, position: Square)