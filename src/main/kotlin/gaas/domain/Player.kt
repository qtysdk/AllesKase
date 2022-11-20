package gaas.domain

import gaas.common.Event
import gaas.common.PlayerHasBeenDead

enum class PlayerAction {
    PEEP, KEEP, DROP
}

data class PlayerActions(val actions: List<PlayerAction>, val index: List<Int>)

class Player(val id: String) {
    var alive: Boolean = true

    val keptCards = mutableListOf<Card>()
    private val events = mutableListOf<Event>()

    fun keepCard(card: Card) {
        if (!alive) {
            throw PlayerHasBeenDead
        }

        keptCards.add(card)
        if (keptCards.filter { it.type == CardType.TRAP }.count() >= 3) {
            alive = false
        }
    }

    fun scores(): Int {
        if (!alive) {
            return 0
        }
        return keptCards.filter {
            it.type == CardType.CHEESE
        }.sumOf { it.value }
    }

    fun events(): List<Event> {
        return this.events
    }

    fun postEvent(event: Event) {
        this.events.add(event)
    }

    override fun toString(): String {
        return "Player(id='$id', alive=$alive, keptCards=$keptCards, events=$events)"
    }

    fun toCompatCardsExpression(): String {
        return keptCards.map { it -> "${it.value}${it.type.name[0]}" }.joinToString(",")
    }

}
