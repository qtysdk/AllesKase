package gaas.domain

class Player(val id: String) {
    var alive: Boolean = true

    private val keptCards = mutableListOf<Card>()

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

}
