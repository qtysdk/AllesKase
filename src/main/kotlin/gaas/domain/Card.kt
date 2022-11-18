package gaas.domain

data class Card(val value: Int, val type: CardType) {
    fun toCompatExpr(): String {
        return "$value${type.name[0]}"
    }
}

enum class CardType {
    TRAP,
    CHEESE
}
