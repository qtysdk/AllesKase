package gaas.domain

class DemoZone : Deck() {

    // TODO the max cards in a demo zone is 6
    fun asEvent(): String {
        // TODO convert to 1T_2C_3C format
        val cardEvent = cards.map { it -> "${it.value}${it.type.name[0]}" }.toList().joinToString("_")
        return "demo-zone: $cardEvent"
    }

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
}

