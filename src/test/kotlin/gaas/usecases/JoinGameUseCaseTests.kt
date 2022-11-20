package gaas.usecases

import gaas.common.IllegalGameStateException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JoinGameUseCaseTests : BaseEndToEndTests() {

    private val getGameViewUseCase = GetGameViewUseCaseImpl(database)

    @Test
    internal fun test_join_game_happy_path() {
        val gameId = givenGameWithPlayers(givenPlayerWithId("i-am-the-host").id)
        (1..5).forEach {
            assertTrue(joinGameUseCase.join(gameId, givenPlayerWithId("join-player-$it").id))
        }

        // join is not allowed when there are 6 players in the game
        val exception = assertFailsWith<IllegalGameStateException> {
            joinGameUseCase.join(gameId, givenPlayerWithId("join-player-5566").id)
        }
        assertEquals("GAME_ROOM_HAS_BEEN_FULL", exception.message)
    }

    @Test
    internal fun test_join_game_dont_allow_after_the_game_started() {
        val gameId = givenGameWithPlayers(givenPlayerWithId(PLAYER_1).id, givenPlayerWithId(PLAYER_2).id)
        startGameUseCase.start(gameId, PLAYER_1)

        // join must not been allowed after the game has started
        val exception = assertFailsWith<IllegalGameStateException> {
            joinGameUseCase.join(gameId, givenPlayerWithId("join-player-5566").id)
        }
        assertEquals("GAME_ROOM_HAS_STARED", exception.message)

    }

    @Test
    internal fun test_join_game_dont_allow_when_the_player_has_joined() {
        val playerId = givenPlayerWithId("i-am-the-host").id
        val gameId = givenGameWithPlayers(playerId)

        val stateBeforeJoinDuplicatedPlayer = getGameViewUseCase.fetch(gameId, playerId)
        assertFalse(joinGameUseCase.join(gameId, playerId))
        val stateAfterJoinDuplicatedPlayer = getGameViewUseCase.fetch(gameId, playerId)
        assertEquals(stateBeforeJoinDuplicatedPlayer, stateAfterJoinDuplicatedPlayer)
    }

}