import gaas.domain.Card
import gaas.domain.CardType
import gaas.domain.GameStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TurnPlayerEndToEndTests : BaseEndToEndTests() {


    @Test
    internal fun turn_player_will_know_dice_value_and_action_list() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)


        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)
        givenDemoZoneWithCards_1C_1T_5T_5C_6T_6T(gameId)
        whenStartTheGame(gameId, PLAYER_1)
        
        // TODO the first player is always the game host, we should pick the first randomly.
        thenTheFirstPlayerGetDiceValue_3_And_ActionListOnlyHasPeepCard(gameId)
        thenAllPlayerKnowsPublicInformationByEvents(gameId)
    }

    private fun thenAllPlayerKnowsPublicInformationByEvents(gameId: String) {
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        println(gameStatus.events)
        assertEquals(
            listOf("demo-zone: 1C_1T_5T_5C_6T_6T", "turn-player: $PLAYER_1"),
            gameStatus.events(2)
        )
    }

    private fun givenDemoZoneWithCards_1C_1T_5T_5C_6T_6T(gameId: String) {
        database.gameMap[gameId]!!.demoZone.apply {
            // whatever in the demo zone, we want to reset it and add new cards
            cards.clear()

            add(Card(1, CardType.CHEESE))
            add(Card(1, CardType.TRAP))
            add(Card(5, CardType.TRAP))
            add(Card(5, CardType.CHEESE))
            add(Card(6, CardType.TRAP))
            add(Card(6, CardType.TRAP))
        }
    }

    private fun thenTheFirstPlayerGetDiceValue_3_And_ActionListOnlyHasPeepCard(gameId: String) {
        val game = database.gameMap[gameId]!!
        val turnPlayer = database.playerMap[PLAYER_1]!!

        assertEquals(game.turn.player, turnPlayer)
        assertEquals(3, game.turn.diceValue)
        assertEquals(listOf("PEEP"), game.turn.actionList)
    }
}