package gaas.domain

import kotlin.streams.toList

class Game {


    lateinit var id: String
    val players = mutableListOf<Player>()
    val events = mutableListOf<String>()
    val providingDeck = Deck()


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

    fun closeGameByEmptyProvidingDeckRule(): Boolean {
        if (!providingDeck.isEmpty()) {
            return false
        }

        this.addEvent("game has ended")

        val sortingPlayers = mutableListOf<Player>().apply { addAll(players) }
        sortingPlayers.sortBy { it.scores() }
        this.addEvent("${sortingPlayers.last().id} won")
        this.announceScores()
        return true
    }
}
