package com.bitrock.model

sealed trait GameRulesViolations {
  def message: String
}

case class InputNotUnderstood(input: String) extends GameRulesViolations {
  override def message: String = s"Input [$input] not understood."
}

case object IllegalState extends GameRulesViolations {
  override def message: String = s"Illegal transition from current state !"
}

case class PlayerNotFound(playerName: String) extends GameRulesViolations {
  override def message: String = s"Player $playerName not found"
}

case class PlayerAlreadyExists(playerName: String) extends GameRulesViolations {
  override def message: String = s"$playerName: already existing player"
}