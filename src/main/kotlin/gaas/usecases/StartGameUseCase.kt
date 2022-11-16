package gaas.usecases

import gaas.common.CannotStartGameByNonHostPlayerException
import gaas.common.CannotStartGameWithTooFewPlayersException
import gaas.common.Events
import gaas.common.GameDoesNotExistException
import gaas.common.GameHasFinishedException
import gaas.common.GameHasStartedException
import gaas.domain.Game
import gaas.repository.Database

interface StartGameUseCase {

    abstract fun start(gameId: String, playerId: String)


}

class StartGameUseCaseImpl(private val database: Database) : StartGameUseCase {
    override fun start(gameId: String, playerId: String) {
        val game = database.findGameById(gameId) ?: throw GameDoesNotExistException
        validatePreconditions(game, playerId)

        game.resetDecksAndDemoZone()
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

    private fun validatePreconditions(game: Game, playerId: String) {
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
    }

}

