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

    val cards = mutableListOf<Card>()

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


}
