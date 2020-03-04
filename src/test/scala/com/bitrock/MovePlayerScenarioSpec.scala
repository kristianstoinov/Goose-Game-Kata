package com.bitrock

import com.bitrock.model.GameBoard.{End, Square, Start}
import com.bitrock.model.{Command, DiceRoll, Player}

class MovePlayerScenarioSpec extends BaseAcceptance {

  info("As a player, I want to move the marker on the board to make the game progress")

  feature("Move a player") {

    scenario("Start") {

      val currentPosition = Start.index
      Given(s"""there are two participants "$pippo" and "$pluto" on space "${Start.name}" """)
      val stateMachine = init(playing(Set(Player(pippo, Square(currentPosition)), Player(pluto, Square(currentPosition)))))

      val (diceRoll1, command1) = rollMv(pippo, DiceRoll(4, 3))
      When(userWrites(command1))
      val movedState1 = stateMachine.nextState(Command(command1))

      val response1 = s"$pippo rolls $diceRoll1. $pippo moves from ${Start.name} to ${diceRoll1.sum}"
      Then(systemResponds(response1))
      assert(movedState1.message === response1)

      val (diceRoll2, cOMMAND2) = rollMv(pluto, DiceRoll(2, 2))
      When(userWrites(cOMMAND2))
      val movedState2 = init(movedState1).nextState(Command(cOMMAND2))

      val response2 = s"$pluto rolls $diceRoll2. $pluto moves from ${Start.name} to ${diceRoll2.sum}"
      Then(systemResponds(response2))
      assert(movedState2.message === response2)

    }
  }

  info("As a player, I win the game if I land on space 63")

  feature("Win") {
    val currentPosition = 60

    scenario("Victory") {
      Given(s"""there is one participant "$pippo" on space "$currentPosition"""")
      val stateMachine = init(playing(Set(Player(pippo, Square(currentPosition)))))

      val (diceRoll1, command1) = rollMv(pippo, DiceRoll(1, 2))
      When(userWrites(command1))
      val movedState1 = stateMachine.nextState(Command(command1))

      val response = s"$pippo rolls $diceRoll1. $pippo moves from $currentPosition to ${currentPosition + diceRoll1.sum}. $pippo Wins!!"
      Then(systemResponds(response))
      assert(movedState1.message === response)
    }


    scenario("Winning with the exact dice shooting") {
      Given(s"""there is one participant "$pippo" on space "$currentPosition"""")
      val stateMachine = init(playing(Set(Player(pippo, Square(currentPosition)))))

      val (diceRoll, command) = rollMv(pippo, DiceRoll(3, 2))
      When(userWrites(command))
      val next = stateMachine.nextState(Command(command))

      val endPosition = End.index - (currentPosition + diceRoll.sum - End.index)
      val response = s"$pippo rolls $diceRoll. $pippo moves from $currentPosition to ${End.name}. $pippo bounces! $pippo returns to $endPosition"
      Then(systemResponds(response))
      assert(next.message === response)

    }
  }

  info("As a player, I want the game throws the dice for me to save effort")

  feature("The game throws the dice") {
    scenario("Dice roll") {
      val currentPosition = 4

      Given(s"""there is one participant "$pippo" on space "$currentPosition"""")
      val stateMachine = init(playing(Set(Player(pippo, Square(currentPosition)))))


      val (diceRoll, command) = (DiceRoll(1, 2), move(pippo))
      When(s"""when the user writes: "$command" (assuming that the dice get $diceRoll)""")
      val next = stateMachine.nextState(mvWithExpRoll(command, diceRoll))

      val response = s"$pippo rolls $diceRoll. $pippo moves from $currentPosition to ${currentPosition + diceRoll.sum}"
      Then(systemResponds(response))
      assert(next.message === response)
    }
  }

  info("""As a player, when I get to the space "The Bridge", I jump to the space "12"""")

  feature("""Space "6" is "The Bridge"""") {
    scenario(s"""Get to "The Bridge"""") {

      val currentPosition = 4

      Given(s"""there is one participant "$pippo" on space "$currentPosition"""")
      val stateMachine = init(playing(Set(Player(pippo, Square(currentPosition)))))


      val (diceRoll, command) = (DiceRoll(1, 1), move(pippo))
      When(s"""when the user writes: "$command" (assuming that the dice get $diceRoll)""")
      val next = stateMachine.nextState(mvWithExpRoll(command, diceRoll))

      val response = s"$pippo rolls $diceRoll. $pippo moves from $currentPosition to ${Square(currentPosition + diceRoll.sum).name}. $pippo jumps to 12"
      Then(systemResponds(response))
      assert(next.message === response)

    }
  }

  info("""As a player, when I get to a space with a picture of "The Goose", I move forward again by the sum of the two dice rolled before""")
  info("""The spaces 5, 9, 14, 18, 23, 27 have a picture of "The Goose"""")

  feature("""If you land on "The Goose", move again""") {
    scenario("Single Jump") {
      val currentPosition = 3
      val endPosition = 7

      Given(s"""there is one participant "$pippo" on space "$currentPosition"""")
      val stateMachine = init(playing(Set(Player(pippo, Square(currentPosition)))))


      val (diceRoll, command) = (DiceRoll(1, 1), move(pippo))
      When(s"""${userWrites(command)} (assuming that the dice get $diceRoll)""")
      val next = stateMachine.nextState(mvWithExpRoll(command, diceRoll))

      val response = s"$pippo rolls $diceRoll. $pippo moves from $currentPosition to ${Square(currentPosition + diceRoll.sum).name}. $pippo moves again and goes to $endPosition"
      Then(systemResponds(response))
      assert(next.message === response)

    }

    scenario("Multiple Jumps") {
      val currentPosition = 10
      val endPosition = 22

      Given(s"""there is one participant "$pippo" on space "$currentPosition"""")
      val stateMachine = init(playing(Set(Player(pippo, Square(currentPosition)))))


      val (diceRoll, command) = (DiceRoll(2, 2), move(pippo))
      When(s"""${userWrites(command)} (assuming that the dice get $diceRoll)""")
      val next = stateMachine.nextState(mvWithExpRoll(command, diceRoll))

      val response = s"$pippo rolls $diceRoll. $pippo moves from $currentPosition to ${Square(currentPosition + diceRoll.sum).name}. $pippo moves again and goes to ${Square(currentPosition + 2 * diceRoll.sum).name}. $pippo moves again and goes to $endPosition"
      Then(systemResponds(response))
      assert(next.message === response)

    }

  }
}
