package gaas.domain

import gaas.common.Event
import java.util.*
import kotlin.streams.toList


class GameStatus {

    var turn: Turn = BEFORE_THE_FIRST_TURN
    var events = listOf<Event>()

    fun events(lastEvents: Int): List<Event> {
        val skip = events.size - lastEvents
        return events.stream().skip(skip.toLong()).toList()
    }

    fun eventAt(index: Int) = events[index]!!


    fun refreshGameStatus(game: Game) {
        this.events = Collections.unmodifiableList(game.events)
        this.turn = game.turn
    }

    override fun toString(): String {
        return "GameStatus(turn=$turn, events=$events)"
    }


}
