package gaas.usecases

import gaas.common.Events
import gaas.repository.Database

enum class PlayerAction {
    PEEP, KEEP, DROP
}

interface PlayerActionUseCase {
    fun doAction(gameId: String, playerId: String, action: String, cardIndex: Int)

}


class PlayerActionUseCaseImpl(val database: Database) : PlayerActionUseCase {
    override fun doAction(gameId: String, playerId: String, action: String, cardIndex: Int) {
        val game = database.gameMap[gameId]!!
        val turn = game.turn
        if (turn.player.id != playerId) {
            throw RuntimeException("PLEASE WAIT FOR YOUR TURN")
        }

        val cardAtDemoZone = game.demoZone[cardIndex]!!

        if (action == PlayerAction.PEEP.name) {
            turn.player.addPrivateMessage("peep, index:$cardIndex, card: ${cardAtDemoZone.value}${cardAtDemoZone.type.name[0]}")
        }

        if (action == PlayerAction.KEEP.name) {
            turn.player.keepCard(cardAtDemoZone)
            game.demoZone.replaceCardAt(cardIndex, game.providingDeck.deal())
            game.postEvent(Events.playerKeepCard(turn.player.id, cardAtDemoZone))
        }

        if (action == PlayerAction.DROP.name) {
            game.demoZone.replaceCardAt(cardIndex, game.providingDeck.deal())
            game.postEvent(Events.playerDropCard(turn.player.id, cardIndex))
            game.droppedDeck.add(cardAtDemoZone)
        }

        // request the next player
        game.nextTurnPlayer()
    }
}
