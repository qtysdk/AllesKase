package gaas.domain

import java.util.Collections
import java.util.Comparator
import java.util.StringJoiner
import kotlin.streams.toList

class Game {


    lateinit var id: String
    val players = mutableListOf<Player>()
    val events = mutableListOf<String>()


    fun join(player: Player) {
        // TODO check the players in 2..4
        // TODO avoid same player join multiple times
        players.add(player)
    }

    fun addEvent(evet: String) {
        this.events.add(evet)
    }

    fun closeGameByOnlyOnePlayerAliveRule(): Boolean {
        if (players.stream().filter { p -> p.alive }.count() > 1L) {
            return false
        }
        this.players.forEach { winner ->
            if (winner.alive) {
                this.addEvent("game has ended")
                this.addEvent("${winner.id} won")
                this.announceScores()
            }
        }


        return true
    }

    private fun announceScores() {
        val scoreList = players.stream().map { it -> "${it.id} got ${it.scores()} scores" }.toList()
        this.addEvent(scoreList.joinToString(", "))
    }
}
