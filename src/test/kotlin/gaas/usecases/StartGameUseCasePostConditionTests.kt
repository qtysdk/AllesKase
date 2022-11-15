package gaas.usecases

import gaas.common.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StartGameUseCasePostConditionTests : BaseEndToEndTests() {


    @Test
    internal fun started_game_should_everything_ready() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)

        whenStartTheGame(gameId, PLAYER_1)

        thenGameHasFollowingPlayersRule(gameId)
        thenGameHasArrangedDecksAndDemoZone(gameId)
    }


    private fun thenGameHasFollowingPlayersRule(gameId: String) {
        val game = database.findGameById(gameId)!!
        assertTrue(
            game.players.size >= 2 && game.players.size <= 4,
            message = "Number of players in a game should be between 2 and 4"
        )
    }

    private fun thenGameHasArrangedDecksAndDemoZone(gameId: String) {
        val game = database.findGameById(gameId)!!

        assertEquals(30, game.providingDeck.size())
        assertEquals(0, game.droppedDeck.size())
        assertEquals(6, game.demoZone.size())
    }

}