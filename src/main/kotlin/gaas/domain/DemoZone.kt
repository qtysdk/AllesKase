package gaas.domain

class DemoZone : Deck() {

    fun createPlayerActions(diceValue: Int): PlayerActions {
        if (!cards.any { card -> card.value == diceValue }) {
            return PlayerActions(listOf(PlayerAction.PEEP), (0 until cards.size).toList())
        }
        val index = cards.mapIndexed { idx, card -> if (card.value == diceValue) idx else -1 }.filter { it >= 0 }
        return PlayerActions(listOf(PlayerAction.KEEP, PlayerAction.DROP), index)
    }

    fun replaceCardAt(cardIndex: Int, card: Card) {
        cards[cardIndex] = card
    }

    operator fun get(cardIndex: Int): Card {
        return this.cards[cardIndex]
    }

    fun toCardValues(): List<Int> {
        return cards.map { it -> it.value }.toList()
    }

}

