package gaas.usecases

import gaas.domain.GameStatus
import gaas.repository.Database


class QueryGameStatus(private val database: Database) {
    fun query(gameId: String): GameStatus {
        val game = database.findGameById(gameId)!!
        val status = GameStatus()
        status.refreshGameStatus(game)


        return status
    }


}
