import gaas.domain.Card
import gaas.domain.CardType
import gaas.domain.GameStatus
import gaas.domain.Player
import gaas.repository.Database
import gaas.usecases.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val PLAYER_1 = "player-1"
private const val PLAYER_2 = "player-2"

class AllesKaseEndToEndTests {

    private val database = Database()
    private val createGameUseCase: CreateGameUseCase = CreateGameUseCaseImpl(database)
    private val joinGameUseCase: JoinGameUseCase = JoinGameUseCaseImpl(database)
    private val startGameUseCase: StartGameUseCase = StartGameUseCaseImpl(database)
    private val queryGameStatus = QueryGameStatus(database)

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

    private fun givenPlayerWithKeptCards(playerId: String, vararg cards: Card) {
        cards.forEach {
            database.playerMap[playerId]!!.keepCard(it)
        }
    }

    private fun thenGameHasEndedAndTheWinnerGotHigherScores(gameId: String) {
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        assertEquals(
            listOf("game has ended", "$PLAYER_2 won", "$PLAYER_2 got 6 scores, $PLAYER_1 got 4 scores"),
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

    private fun whenStartTheGame(gameId: String, playerId: String) {
        startGameUseCase.start(gameId, playerId)
    }

    private fun givenGameWithPlayers(vararg playerIds: String): String {
        val gameId = createGameUseCase.create(playerIds[0])
        if (playerIds.size > 1) {
            playerIds.toList().stream().skip(1).forEach {
                assertTrue(joinGameUseCase.join(gameId, it))
            }
        }
        return gameId
    }


    private fun givenPlayerWithId(playerId: String): Player {
        val p = Player(playerId)
        database.playerMap[playerId] = p
        return p
    }

    private fun givenPlayerNotAliveStatus(s: String) {
        database.playerMap[s]!!.alive = false
    }
}