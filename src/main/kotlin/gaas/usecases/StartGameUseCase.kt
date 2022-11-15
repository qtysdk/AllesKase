package gaas.usecases

import gaas.common.Events
import gaas.repository.Database

interface StartGameUseCase {
    abstract fun start(gameId: String, playerId: String)

}

class StartGameUseCaseImpl(private val database: Database) : StartGameUseCase {
    override fun start(gameId: String, playerId: String) {
        // TODO check player own this game
        // send started event
        val game = database.findGameById(gameId)
        game.postEvent(Events.GAME_STARTED)


        if (game.closeGameByOnlyOnePlayerAliveRule()) {
            return
        }

        if (game.closeGameByEmptyProvidingDeckRule()) {
            return
        }

        game.nextTurnPlayer()

        return
    }

}

