package gaas.repository

import gaas.domain.Game
import gaas.domain.Player
import java.util.UUID

class Database {

    val gameMap = mutableMapOf<String, Game>()
    val playerMap = mutableMapOf<String, Player>()

    fun save(game: Game): Game {
        game.id = UUID.randomUUID().toString()
        gameMap[game.id] = game
        return game
    }

    fun findPlayerById(playerId: String): Player? {
        return playerMap[playerId]
    }

    fun findGameById(gameId: String): Game? {
        return gameMap[gameId]
    }
}