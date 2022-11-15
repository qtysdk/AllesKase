package gaas.usecases

import gaas.common.*
import gaas.repository.Database

interface StartGameUseCase {
    abstract fun start(gameId: String, playerId: String)

}

class StartGameUseCaseImpl(private val database: Database) : StartGameUseCase {
    override fun start(gameId: String, playerId: String) {
        val game = database.findGameById(gameId)

        if (game == null) {
            throw GameDoesNotExistException
        }

        if (game.events.any { it.message == Events.GAME_STARTED.message }) {
            throw GameHasStartedException
        }

        if (game.events.any { it.message == Events.GAME_ENDED.message }) {
            throw GameHasFinishedException
        }

        val hostPlayer = database.findPlayerById(playerId)
        if (game.host != hostPlayer) {
            throw CannotStartGameByNonHostPlayerException
        }

        if (game.players.size < 2) {
            throw CannotStartGameWithTooFewPlayersException
        }

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

