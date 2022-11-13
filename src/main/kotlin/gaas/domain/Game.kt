package gaas.domain

import java.util.StringJoiner

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

    fun canEndByOnlyOnePlayerAlive(): Boolean {
        return players.stream().filter { p -> p.alive }.count() == 1L
    }
}
