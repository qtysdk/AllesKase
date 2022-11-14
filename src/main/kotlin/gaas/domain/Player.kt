package gaas.domain

import java.util.Collections
import java.util.StringJoiner

class Player(val id: String) {
    var alive: Boolean = true

    private val keptCards = mutableListOf<Card>()
    private val privateMessage = mutableListOf<String>()

    fun keepCard(card: Card) {
        this.keptCards.add(card)
    }

    fun scores(): Int {
        if (!alive) {
            return 0
        }
        return keptCards.filter {
            it.type == CardType.CHEESE
        }.sumOf { it.value }
    }

    fun privateMessages(): List<String> {
        return this.privateMessage
    }

    fun addPrivateMessage(message: String) {
        this.privateMessage.add(message)
    }

}
