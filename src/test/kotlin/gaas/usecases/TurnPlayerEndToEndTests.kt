package gaas.usecases

import gaas.common.DefaultGameInitializer
import gaas.common.Events
import gaas.common.GameInitializer
import gaas.domain.Card
import gaas.domain.CardType
import gaas.domain.DemoZone
import gaas.domain.Dice
import gaas.domain.Game
import gaas.domain.GameStatus
import gaas.domain.PlayerAction
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class TurnPlayerEndToEndTests : BaseEndToEndTests() {

    val playerActionUseCase: PlayerActionUseCase = PlayerActionUseCaseImpl(database)

    @Test
    internal fun turn_player_will_know_dice_value_and_action_list() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)


        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)
        givenDemoZoneWithCards_1C_1T_5T_5C_6T_6T(gameId)

        givenDiceAlwaysReturns3(gameId)
        whenStartTheGame(gameId, PLAYER_1)

        // TODO the first player is always the game host, we should pick the first randomly.
        thenTheFirstPlayerGetDiceValue_3_And_ActionListOnlyHasPeepCard(gameId)
        thenAllPlayerKnowsPublicInformationByEvents(gameId)

        whenWrongPlayerDoActionThenGotException(gameId)
        whenTurnPlayerDoActionThenSwitchToTheNextPlayer(gameId)

    }

    private fun givenDiceAlwaysReturns3(gameId: String) {
        val game = database.findGameById(gameId)!!
        game.dice = mockk<Dice>()
        every { game.dice.roll() } returns 3
    }

    private fun whenTurnPlayerDoActionThenSwitchToTheNextPlayer(gameId: String) {
        playerActionUseCase.doAction(gameId, PLAYER_1, "PEEP", 0)
        // player 1 got the private message for peep the card
        assertEquals(database.playerMap[PLAYER_1]!!.privateMessages(), listOf("peep, index:0, card: 1C"))

        // player changed
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        assertEquals(gameStatus.turn.player.id, PLAYER_2)

    }

    private fun whenWrongPlayerDoActionThenGotException(gameId: String) {
        assertThrows<RuntimeException> {
            playerActionUseCase.doAction(gameId, PLAYER_2, "WHATEVER_ACTION", 0)
        }

    }

    private fun thenAllPlayerKnowsPublicInformationByEvents(gameId: String) {
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        var events = gameStatus.events(2)

        val demoZone = mockk<DemoZone>()
        every { demoZone.toCompatCardsExpression() } returns "1C,1T,5T,5C,6T,6T"

        assertEquals(Events.demoZone(demoZone), events[0])
        assertEquals(Events.turnPlayer(PLAYER_1), events[1])
    }

    private fun givenDemoZoneWithCards_1C_1T_5T_5C_6T_6T(gameId: String) {
        database.findGameById(gameId)!!.cardsInitializer = object : GameInitializer {
            override fun resetCards(game: Game) {
                game.demoZone.apply {

                    // invoke the origin initializer to avoid the game becoming ended.
                    DefaultGameInitializer().resetCards(game)

                    // whatever in the demo zone, we want to reset it and add new cards
                    game.demoZone.clear()

                    add(Card(1, CardType.CHEESE))
                    add(Card(1, CardType.TRAP))
                    add(Card(5, CardType.TRAP))
                    add(Card(5, CardType.CHEESE))
                    add(Card(6, CardType.TRAP))
                    add(Card(6, CardType.TRAP))
                }
            }
        }

    }

    private fun thenTheFirstPlayerGetDiceValue_3_And_ActionListOnlyHasPeepCard(gameId: String) {
        val game = database.gameMap[gameId]!!
        val turnPlayer = database.playerMap[PLAYER_1]!!



        assertEquals(game.turn.player, turnPlayer)
        assertEquals(3, game.turn.diceValue)
        println(game.turn.actionList)
        assertEquals(listOf(PlayerAction.PEEP), game.turn.actionList)
    }
}