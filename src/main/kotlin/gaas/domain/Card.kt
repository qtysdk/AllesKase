package gaas.domain

data class Card(val value: Int, val type: CardType)

enum class CardType {
    TRAP,
    CHEESE
}
