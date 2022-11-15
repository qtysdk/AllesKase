package gaas.domain

import gaas.common.Event
import gaas.common.Events
import org.slf4j.LoggerFactory
import kotlin.streams.toList

class Game {

    val logger = LoggerFactory.getLogger(Game::javaClass.name)

    lateinit var id: String
    val players = mutableListOf<Player>()
    val events = mutableListOf<Event>()

    val demoZone = DemoZone()
    val providingDeck = Deck()

    var turn: Turn = BEFORE_THE_FIRST_TURN
    val dice = Dice()

    init {
        logger.info("Game[$this] created")
    }


    fun join(player: Player) {
        // TODO check the players in 2..4
        // TODO avoid same player join multiple times
        players.add(player)
    }

    fun postEvent(event: Event) {
        logger.info("post-event: $event")
        this.events.add(event)
    }

    fun closeGameByOnlyOnePlayerAliveRule(): Boolean {
        if (players.stream().filter { p -> p.alive }.count() > 1L) {
            return false
        }
        this.players.forEach { winner ->
            if (winner.alive) {
                this.postEvent(Events.GAME_ENDED)
                this.postEvent(Events.winner(winner.id))
                this.announceScores()
            }
        }


        return true
    }

    private fun announceScores() {
        val scoreList = players.stream().map { it -> "${it.id} got ${it.scores()} scores" }.toList()
        this.postEvent(Events.scoreList(scoreList.joinToString(", ")))
    }

    fun closeGameByEmptyProvidingDeckRule(): Boolean {
        if (!providingDeck.isEmpty()) {
            return false
        }

        this.postEvent(Events.GAME_ENDED)

        val sortingPlayers = mutableListOf<Player>().apply { addAll(players) }
        sortingPlayers.sortBy { it.scores() }
        this.postEvent(Events.winner(sortingPlayers.last().id))
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

        this.postEvent(Events.demoZone(this.demoZone.asEvent()))
        // TODO pick the "first" next player
        if (turn == BEFORE_THE_FIRST_TURN) {
            // it is time for pick the first player
            val player = players[0]
            turn = Turn(player, dice.roll(), listOf("PEEP"))
            postEvent(Events.turnPlayer(player.id))
            return
        }

        var next = players.indexOf(turn.player) + 1
        if (next >= players.size) {
            next = 0
        }

        val player = players[next]
        turn = Turn(player, dice.roll(), listOf("PEEP"))
        postEvent(Events.turnPlayer(player.id))
        return
    }
}
