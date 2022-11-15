package gaas.usecases

import gaas.common.Events
import gaas.common.GameInitializer
import gaas.domain.Card
import gaas.domain.CardType
import gaas.domain.Game
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
        givenGameWithEmptyProvidingDeck(gameId)
        whenStartTheGame(gameId, PLAYER_1)

        thenGameHasEndedAndTheWinnerGotHigherScores(gameId)
    }

    private fun givenGameWithEmptyProvidingDeck(gameId: String) {
        database.findGameById(gameId)!!.cardsInitializer = object : GameInitializer {
            override fun resetCards(game: Game) {
                // do nothing, we expect an empty deck here
            }
        }
    }

    private fun thenGameHasEndedAndTheWinnerGotHigherScores(gameId: String) {
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        assertEquals(Events.GAME_STARTED, gameStatus.eventAt(0))
        assertEquals(Events.GAME_ENDED, gameStatus.eventAt(1))
        assertEquals(Events.winner(PLAYER_2), gameStatus.eventAt(2))
        assertEquals(Events.scoreList("$PLAYER_1 got 4 scores, $PLAYER_2 got 6 scores"), gameStatus.eventAt(3))
    }

    private fun thenGameHasEndedAndAlivePlayerIsTheWinner(gameId: String) {
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        assertEquals(Events.GAME_STARTED, gameStatus.eventAt(0))
        assertEquals(Events.GAME_ENDED, gameStatus.eventAt(1))
        assertEquals(Events.winner(PLAYER_2), gameStatus.eventAt(2))
        assertEquals(Events.scoreList("player-1 got 0 scores, player-2 got 0 scores"), gameStatus.eventAt(3))
    }

}