import gaas.domain.Card
import gaas.domain.CardType
import gaas.domain.GameStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class AllesKaseEndToEndTests : BaseEndToEndTests() {

    @Test
    internal fun the_shortest_path_from_game_started_to_the_end_because_only_1_player_alive() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        givenPlayerNotAliveStatus(PLAYER_1)

        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)
        whenStartTheGame(gameId, PLAYER_1)

        thenGameHasEndedAndAlivePlayerIsTheWinner(gameId)
    }

    @Test
    internal fun the_shortest_path_from_game_started_to_the_end_because_providing_deck_is_empty() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        givenPlayerWithKeptCards(PLAYER_1, Card(2, CardType.TRAP), Card(4, CardType.CHEESE))
        givenPlayerWithKeptCards(PLAYER_2, Card(5, CardType.CHEESE), Card(1, CardType.CHEESE))

        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)
        whenStartTheGame(gameId, PLAYER_1)

        thenGameHasEndedAndTheWinnerGotHigherScores(gameId)
    }

    private fun thenGameHasEndedAndTheWinnerGotHigherScores(gameId: String) {
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        assertEquals(
            listOf("game has ended", "$PLAYER_2 won", "$PLAYER_1 got 4 scores, $PLAYER_2 got 6 scores"),
            gameStatus.events(3)
        )
    }

    private fun thenGameHasEndedAndAlivePlayerIsTheWinner(gameId: String) {
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        assertEquals(
            listOf("game has ended", "$PLAYER_2 won", "$PLAYER_1 got 0 scores, $PLAYER_2 got 0 scores"),
            gameStatus.events(3)
        )
    }


}