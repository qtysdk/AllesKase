package gaas.usecases

import gaas.common.PlayerHasBeenInTheGame
import gaas.domain.Game
import gaas.repository.Database

interface JoinGameUseCase {
    abstract fun join(gameId: String, playerId: String): Boolean

}

class JoinGameUseCaseImpl(private val database: Database) : JoinGameUseCase {
    override fun join(gameId: String, playerId: String): Boolean {
        val game: Game = database.findGameById(gameId)!!
        try {
            game.join(database.findPlayerById(playerId)!!)
        } catch (e: Exception) {
            // duplicated join, just return false enough
            if (e == PlayerHasBeenInTheGame) {
                return false
            }
            throw e
        }
        return true
    }

}

