package gaas.repository

import gaas.domain.Game
import gaas.domain.Player
import java.util.*

class Database {


    val gameMap = mutableMapOf<String, Game>()
    val playerMap = mutableMapOf<String, Player>()


    init {
        playerMap["fake-player-1"] = Player("fake-player-1")
        playerMap["fake-player-2"] = Player("fake-player-2")
        playerMap["fake-player-3"] = Player("fake-player-3")
        playerMap["fake-player-4"] = Player("fake-player-4")
    }

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