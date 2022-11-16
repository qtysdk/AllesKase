package gaas.domain

import gaas.common.DefaultFirstPlayerChooser
import gaas.common.DefaultGameInitializer
import gaas.common.Event
import gaas.common.Events
import gaas.common.FirstPlayerChooser
import gaas.common.GameInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.streams.toList

class Game {


    private val logger: Logger = LoggerFactory.getLogger("Game")

    lateinit var id: String
    lateinit var host: Player

    val players = mutableListOf<Player>()
    val events = mutableListOf<Event>()

    val demoZone = DemoZone()
    val providingDeck = Deck()
    val droppedDeck = Deck()

    var turn: Turn = BEFORE_THE_FIRST_TURN
    var dice = Dice()
    var cardsInitializer: GameInitializer = DefaultGameInitializer()
    var firstPlayerChooser: FirstPlayerChooser = DefaultFirstPlayerChooser()

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
        if (turn == BEFORE_THE_FIRST_TURN) {
            // it is time for pick the first player
            val player = firstPlayerChooser.pickTheFirstTurnPlayer(this)
            turn = createTurn(player)
            postEvent(Events.turnPlayer(player.id))
            return
        }

        var next = players.indexOf(turn.player) + 1
        if (next >= players.size) {
            next = 0
        }

        val player = players[next]
        turn = createTurn(player)
        postEvent(Events.turnPlayer(player.id))
        return
    }

    private fun createTurn(player: Player): Turn {
        val diceValue = dice.roll()
        val actions = demoZone.createPlayerActions(diceValue)
        return Turn(player, diceValue, actions.actions, actions.index)
    }

    fun resetDecksAndDemoZone() {
        cardsInitializer.resetCards(this)
    }


}
