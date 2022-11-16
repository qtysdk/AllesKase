package gaas.domain

class Dice {
    fun roll(): Int {
        return (1..6).random()
    }

}
