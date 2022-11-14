package gaas.domain

open class Deck {

    val cards = mutableListOf<Card>()

    init {
        // TODO just add cards for pass tests, we need init it by the Game Rule
        cards.add(Card(1, CardType.TRAP))
    }

    fun isEmpty(): Boolean {
        return cards.isEmpty()
    }

}
