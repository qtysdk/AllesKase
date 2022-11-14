package gaas.domain

import kotlin.streams.toList

class Game {


    lateinit var id: String
    val players = mutableListOf<Player>()
    val events = mutableListOf<String>()

    val demoZone = DemoZone()
    val providingDeck = Deck()

    var turn: Turn = BEFORE_THE_FIRST_TURN
    val dice = Dice()


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

    fun nextTurnPlayer() {
        if (closeGameByOnlyOnePlayerAliveRule()) {
            return
        }

        if (closeGameByEmptyProvidingDeckRule()) {
            return
        }

        this.addEvent(this.demoZone.asEvent())
        // TODO pick the "first" next player
        if (turn == BEFORE_THE_FIRST_TURN) {
            // it is time for pick the first player
            val player = players[0]
            turn = Turn(player, dice.roll(), listOf("PEEP"))
            addEvent("turn-player: ${player.id}")
            return
        }

        var next = players.indexOf(turn.player) + 1
        if (next >= players.size) {
            next = 0
        }

        val player = players[next]
        turn = Turn(player, dice.roll(), listOf("PEEP"))
        addEvent("turn-player: ${player.id}")
        return
    }
}
