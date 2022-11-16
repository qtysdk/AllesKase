package gaas.domain

class Player(val id: String) {
    var alive: Boolean = true

    val keptCards = mutableListOf<Card>()
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

    override fun toString(): String {
        return "Player(id='$id', alive=$alive, keptCards=$keptCards, privateMessage=$privateMessage)"
    }


}
