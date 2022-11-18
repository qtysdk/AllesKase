package gaas.ports

import kotlinx.serialization.Serializable

@Serializable
data class PlayerOutput(
    val playerId: String,
    val keptCards: String,
    val score: Int,
    val alive: Boolean
)

@Serializable
data class TurnOutput(
    val player: PlayerOutput,
    val diceValue: Int,
    val actionList: List<String>,
    val actionIndex: List<Int>
)

@Serializable
data class DemoZoneOutput(val cards: String)

@Serializable
data class GameViewOutput(
    val players: List<PlayerOutput>,
    val turn: TurnOutput,
    val demoZone: DemoZoneOutput
)