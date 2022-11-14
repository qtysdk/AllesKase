package gaas.usecases

import gaas.repository.Database

interface StartGameUseCase {
    abstract fun start(gameId: String, playerId: String)

}

class StartGameUseCaseImpl(private val database: Database) : StartGameUseCase {
    override fun start(gameId: String, playerId: String) {
        // TODO check player own this game
        // send started event
        val game = database.findGameById(gameId)
        game.addEvent("game has stated")


        if (game.closeGameByOnlyOnePlayerAliveRule()) {
            return
        }

        // TODO case 2

        // otherwise, make the first player actionable
        game.addEvent("player-? act")
        return
    }

}

