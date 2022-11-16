package gaas.domain

private data class CardSpec(val value: Int, val numbOfCheese: Int)

private val cardSpec = listOf(
    CardSpec(1, 4),
    CardSpec(2, 4),
    CardSpec(3, 3),
    CardSpec(4, 3),
    CardSpec(5, 2),
    CardSpec(6, 2),
)


open class Deck {

    protected val cards = mutableListOf<Card>()

    fun add(card: Card) {
        cards.add(card)
    }

    fun isEmpty(): Boolean {
        return cards.isEmpty()
    }

    fun size(): Int {
        return cards.size
    }

    fun resetForProvidingDeck() {
        cardSpec.forEach { spec ->
            run {
                (1..spec.numbOfCheese).forEach {
                    cards.add(Card(spec.value, CardType.CHEESE))
                }
                (1..6 - spec.numbOfCheese).forEach {
                    cards.add(Card(spec.value, CardType.TRAP))
                }
            }
        }

        cards.shuffle()
    }

    fun deal(): Card {
        try {
            return cards.removeLast()
        } catch (e: Exception) {
            // no cards here
            return Card(-1, CardType.CHEESE)
        }
    }

    fun selfValidateForProvidingDeck() {
        assert(36 == cards.size)
        assert(6 == cards.filter { it.value == 1 }.size)

        assert(6 == cards.filter { it.value == 1 }.size)
        assert(4 == cards.filter { it.value == 1 && it.type == CardType.CHEESE }.size)

        assert(6 == cards.filter { it.value == 2 }.size)
        assert(4 == cards.filter { it.value == 2 && it.type == CardType.CHEESE }.size)

        assert(6 == cards.filter { it.value == 3 }.size)
        assert(3 == cards.filter { it.value == 3 && it.type == CardType.CHEESE }.size)

        assert(6 == cards.filter { it.value == 4 }.size)
        assert(3 == cards.filter { it.value == 4 && it.type == CardType.CHEESE }.size)

        assert(6 == cards.filter { it.value == 5 }.size)
        assert(2 == cards.filter { it.value == 5 && it.type == CardType.CHEESE }.size)

        assert(6 == cards.filter { it.value == 6 }.size)
        assert(2 == cards.filter { it.value == 6 && it.type == CardType.CHEESE }.size)
    }

    fun last(): Card {
        return cards.last()
    }

    fun clear() {
        cards.clear()
    }


}
