package gaas.domain

class DemoZone : Deck() {

    // TODO the max cards in a demo zone is 6
    fun add(card: Card) {
        cards.add(card)
    }

    fun asEvent(): String {
        // TODO convert to 1T_2C_3C format
        val cardEvent = cards.map { it -> "${it.value}${it.type.name[0]}" }.toList().joinToString("_")
        return "demo-zone: $cardEvent"
    }
}
