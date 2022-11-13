package gaas.domain

import java.util.Collections
import kotlin.streams.toList

class GameStatus {

    var events = listOf<String>()

    fun events(lastEvents: Int): List<String> {
        val skip = events.size - lastEvents
        return events.stream().skip(skip.toLong()).toList()
    }

    fun refreshGameStatus(game: Game) {
        this.events = Collections.unmodifiableList(game.events)
    }

}
