package gaas.common

import gaas.domain.Game
import gaas.domain.Player

interface FirstPlayerChooser {
    fun pickTheFirstTurnPlayer(game: Game): Player
}

class DefaultFirstPlayerChooser : FirstPlayerChooser {
    override fun pickTheFirstTurnPlayer(game: Game): Player {
        val pick = (0 until game.players.size).random()
        return game.players[pick]
    }
}

