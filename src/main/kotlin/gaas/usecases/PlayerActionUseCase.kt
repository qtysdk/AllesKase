package gaas.usecases

import gaas.common.Events
import gaas.common.InvalidCardIndexException
import gaas.common.InvalidPlayerActionException
import gaas.common.InvalidTurnPlayerException
import gaas.domain.Game
import gaas.domain.PlayerAction
import gaas.repository.Database

interface PlayerActionUseCase {
    fun doAction(gameId: String, playerId: String, action: String, cardIndex: Int)

}

class PlayerActionUseCaseImpl(val database: Database) : PlayerActionUseCase {
    override fun doAction(gameId: String, playerId: String, action: String, cardIndex: Int) {
        val game = database.gameMap[gameId]!!
        val turn = game.turn
        if (turn.player.id != playerId) {
            throw InvalidTurnPlayerException
        }

        validateActionRequest(game, action)
        validateCardIndex(game, cardIndex)

        val cardAtDemoZone = game.demoZone[cardIndex]

        if (action == PlayerAction.PEEP.name) {
            turn.player.postEvent(Events.playerPeepCardInPrivate(playerId, cardAtDemoZone))
            game.postEvent(Events.playerPeepCard(playerId, cardIndex))
        }

        if (action == PlayerAction.KEEP.name) {
            turn.player.keepCard(cardAtDemoZone)
            game.demoZone.replaceCardAt(cardIndex, game.providingDeck.deal())
            game.postEvent(Events.playerKeepCard(turn.player.id, cardIndex))
        }

        if (action == PlayerAction.DROP.name) {
            game.demoZone.replaceCardAt(cardIndex, game.providingDeck.deal())
            game.postEvent(Events.playerDropCard(turn.player.id, cardIndex))
            game.droppedDeck.add(cardAtDemoZone)
        }

        // request the next player
        game.nextTurnPlayer()
    }

    private fun validateCardIndex(game: Game, cardIndex: Int) {
        if (!game.turn.actionIndex.contains(cardIndex)) {
            throw InvalidCardIndexException
        }
    }

    private fun validateActionRequest(game: Game, action: String) {
        try {
            if (game.turn.actionList.any { it == PlayerAction.valueOf(action) }) {
                return
            }
        } catch (ignored: Exception) {
            // failed by PlayerAction.valueOf
        }
        throw InvalidPlayerActionException
    }
}
