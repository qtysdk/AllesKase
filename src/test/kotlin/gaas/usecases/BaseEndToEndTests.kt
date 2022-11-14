package gaas.usecases

import gaas.domain.Card
import gaas.domain.Player
import gaas.repository.Database
import kotlin.test.assertTrue

abstract class BaseEndToEndTests {
    val PLAYER_1 = "player-1"
    val PLAYER_2 = "player-2"
    val database = Database()
    val createGameUseCase: CreateGameUseCase = CreateGameUseCaseImpl(database)
    val joinGameUseCase: JoinGameUseCase = JoinGameUseCaseImpl(database)
    val startGameUseCase: StartGameUseCase = StartGameUseCaseImpl(database)
    val queryGameStatus = QueryGameStatus(database)

    protected fun givenPlayerWithKeptCards(playerId: String, vararg cards: Card) {
        cards.forEach {
            database.playerMap[playerId]!!.keepCard(it)
        }
    }

    protected fun whenStartTheGame(gameId: String, playerId: String) {
        startGameUseCase.start(gameId, playerId)
    }

    protected fun givenGameWithPlayers(vararg playerIds: String): String {
        val gameId = createGameUseCase.create(playerIds[0])
        if (playerIds.size > 1) {
            playerIds.toList().stream().skip(1).forEach {
                assertTrue(joinGameUseCase.join(gameId, it))
            }
        }
        return gameId
    }

    protected fun givenPlayerWithId(playerId: String): Player {
        val p = Player(playerId)
        database.playerMap[playerId] = p
        return p
    }

    protected fun givenPlayerNotAliveStatus(s: String) {
        database.playerMap[s]!!.alive = false
    }
}