import gaas.domain.GameStatus
import gaas.domain.Player
import gaas.repository.Database
import gaas.usecases.*
import org.junit.jupiter.api.Test
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList
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

        thenGameHasEndedAndPlayer2IsTheWinner(gameId, PLAYER_2)
    }

    private fun thenGameHasEndedAndPlayer2IsTheWinner(gameId: String, playerId: String) {
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        assertEquals(listOf("game has ended", "$playerId won"), gameStatus.events(2))
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