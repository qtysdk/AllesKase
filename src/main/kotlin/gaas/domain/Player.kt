package gaas.domain

class Player(val id: String) {
    var alive: Boolean = true

    private val keptCards = mutableListOf<Card>()

    fun keepCard(card: Card) {
        this.keptCards.add(card)
    }

}
