package gaas.usecases

import gaas.repository.Database
import java.lang.RuntimeException

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

        if (action == PlayerAction.PEEP.name) {
            val card = game.demoZone.cards[cardIndex]!!
            turn.player.addPrivateMessage("peep, index:$cardIndex, card: ${card.value}${card.type.name[0]}")
        }

        // TODO implement KEEP
        // TODO implement DROP

        // request the next player
        game.nextTurnPlayer()
    }
}
