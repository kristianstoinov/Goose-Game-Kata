package com.bitrock

import com.bitrock.model.GameBoard.Start
import com.bitrock.model.{Command, Player}

class AddPlayerScenarioSpec extends BaseAcceptance {

  info("As a player, I want to add me and others to the game so that we can play.")

  feature("Add players") {

    scenario("Add new players") {

      Given("there are no participants")
      val stateMachine = init(add(Set()))

      val commandFirst = add(pippo)
      When(userWrites(commandFirst))
      val onePlayerState = stateMachine.nextState(Command(commandFirst))

      val (responseOnePlayerAdded, responseSecondPlayerAdded) = (s"players: $pippo", s"players: $pippo, $pluto")
      Then(systemResponds(responseOnePlayerAdded))
      assert(onePlayerState.message === responseOnePlayerAdded)

      val commandSecond = add(pluto)
      When(userWrites(commandSecond))
      val twoPlayersState = init(onePlayerState).nextState(Command(commandSecond))

      Then(systemResponds(responseSecondPlayerAdded))
      assert(twoPlayersState.message === responseSecondPlayerAdded)

    }

    scenario("Duplicated players") {
      Given(s"there is already a participant $pippo")
      val onePlayerState = init(add(Set(Player(pippo,Start))))

      val command = add(pippo)
      When(userWrites(command))
      val next = onePlayerState.nextState(Command(command))

      val response = s"$pippo: already existing player"
      Then(systemResponds(response))
      assert(next.message === response)
    }
  }

}
