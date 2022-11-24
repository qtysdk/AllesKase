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
data class DeckTopCard(val value: Int, val numberOfCards: Int)

@Serializable
data class TurnOutput(
    val player: PlayerOutput,
    val diceValue: Int,
    val actionList: List<String>,
    val actionIndex: List<Int>
)

@Serializable
data class EventOutput(
    val type: String,
    val createdAt: String,
    val data: String,
    val playerId: String?
)

@Serializable
data class CardActions(val index: Int, val value: Int, val actions: List<String>)

@Serializable
data class GameViewOutput(
    val gameId: String,
    val players: List<PlayerOutput>,
    val turn: TurnOutput,
    val demoZone: List<CardActions>,
    val providingDeck: DeckTopCard,
    val droppedDeck: DeckTopCard,
    val events: List<EventOutput>
)