package gaas.usecases

import gaas.common.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StartGameUseCaseIllegalStateTests : BaseEndToEndTests() {

    @Test
    internal fun cannot_start_the_game_less_than_two_players() {
        givenPlayerWithId(PLAYER_1)
        val gameId = givenGameWithPlayers(PLAYER_1)

        val exception = assertFailsWith<IllegalGameStateException>(
            block = { whenStartTheGame(gameId, PLAYER_1) }
        )
        assertEquals(CannotStartGameWithTooFewPlayersException.message, exception.message)
    }

    @Test
    internal fun cannot_start_the_game_when_the_player_is_not_the_host() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)

        val exception = assertFailsWith<IllegalGameStateException>(
            block = { whenStartTheGame(gameId, PLAYER_2) }
        )
        assertEquals(CannotStartGameByNonHostPlayerException.message, exception.message)


    }

    @Test
    internal fun not_such_game() {
        val exception = assertFailsWith<IllegalGameStateException>(
            block = { whenStartTheGame("no-such-game-id", PLAYER_2) }
        )
        assertEquals(GameDoesNotExistException.message, exception.message)
    }

    @Test
    internal fun cannot_start_a_started_game() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)

        // assume the game has started
        database.findGameById(gameId)!!.postEvent(Events.GAME_STARTED)

        val exception = assertFailsWith<IllegalGameStateException> {
            whenStartTheGame(gameId, PLAYER_1)
        }
        assertEquals(GameHasStartedException.message, exception.message)
    }

    @Test
    internal fun cannot_start_a_finished_game() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)

        // assume the game has started
        database.findGameById(gameId)!!.postEvent(Events.GAME_ENDED)

        val exception = assertFailsWith<IllegalGameStateException> {
            whenStartTheGame(gameId, PLAYER_1)
        }
        assertEquals(GameHasFinishedException.message, exception.message)
    }


}