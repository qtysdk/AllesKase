package gaas.domain

data class Turn(val player: Player, val diceValue: Int, val actionList: List<String>, val actionIndex: List<Int>) {

}

val BEFORE_THE_FIRST_TURN = Turn(Player("-"), 0, emptyList(), emptyList())