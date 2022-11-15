package gaas.usecases

import gaas.domain.Game
import gaas.repository.Database

interface JoinGameUseCase {
    abstract fun join(gameId: String, playerId: String): Boolean

}

class JoinGameUseCaseImpl(private val database: Database) : JoinGameUseCase {
    override fun join(gameId: String, playerId: String): Boolean {
        val game: Game = database.findGameById(gameId)!!

        // TODO check player exists or do exception handleing
        game.join(database.findPlayerById(playerId)!!)
        return true
    }

}

