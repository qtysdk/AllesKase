import gaas.domain.GameStatus
import gaas.domain.Player
import gaas.repository.Database
import gaas.usecases.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllesKaseEndToEndTests {

    @Test
    internal fun the_shortest_path_from_game_started_to_the_end_because_only_1_player_alive() {
        val player1 = Player("player-1")
        val player2 = Player("player-2")
        player1.alive = false

        val database = Database()
        database.playerMap.put(player1.id, player1)
        database.playerMap.put(player2.id, player2)

        val createGameUseCase: CreateGameUseCase = CreateGameUseCaseImpl(database)
        val joinGameUseCase: JoinGameUseCase = JoinGameUseCaseImpl(database)
        val startGameUseCase: StartGameUseCase = StartGameUseCaseImpl(database)
        val queryGameStatus = QueryGameStatus(database)

        val gameId = createGameUseCase.create(player1.id)
        assertTrue(joinGameUseCase.join(gameId, player2.id))


        // command: 設置遊戲
        startGameUseCase.start(gameId, player1.id)


        // 對 domain.Player 做了什麼..
        val gameStatus: GameStatus = queryGameStatus.query(gameId)
        assertEquals(listOf("game has ended", "player-2 won"), gameStatus.events(2))

    }
}