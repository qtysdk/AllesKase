package gaas.usecases

import gaas.domain.BEFORE_THE_FIRST_TURN
import gaas.domain.Game
import gaas.domain.GameStatus
import gaas.repository.Database
import web.GameStatusResponse

class QueryGameStatus(private val database: Database) {
    fun query(gameId: String, response: GameStatusResponse?): GameStatus {
        val game = database.findGameById(gameId)!!
        val status = GameStatus()
        status.refreshGameStatus(game)

        if (response != null) {
            buildResponse(game, response)
        }
        return status
    }

    private fun buildResponse(game: Game, response: GameStatusResponse) {
        game.players.forEach {
            response.addPlayer(
                it.id,
                it.alive,
                it.toCompatCardsExpression()
            )
        }
        if (game.turn == BEFORE_THE_FIRST_TURN) {
        } else {
            response.turn(
                game.turn.player.id,
                game.turn.diceValue,
                game.turn.actionList.map { it.name }.toList().joinToString(","),
                game.turn.actionIndex.joinToString(",")
            )
        }

        response.demoZone = game.demoZone.toCompatCardsExpression()
    }

}
