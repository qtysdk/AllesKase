package gaas.usecases

import gaas.domain.Game
import gaas.repository.Database

interface CreateGameUseCase {
    abstract fun create(playerId: String): String

}

class CreateGameUseCaseImpl(private val database: Database) : CreateGameUseCase {
    override fun create(playerId: String): String {
        val game = Game()
        val player = database.findPlayerById(playerId)
        game.host = player!!
        game.join(player!!)
        return database.save(game).id
    }

}
