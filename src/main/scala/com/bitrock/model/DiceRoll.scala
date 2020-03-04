package com.bitrock.model

final case class DiceRoll(first: Int, second: Int) {
  def sum: Int = first + second

  override def toString: String = s"$first, $second"
}