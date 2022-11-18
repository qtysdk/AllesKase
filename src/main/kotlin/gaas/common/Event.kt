package gaas.common

import gaas.domain.Card
import java.time.LocalDateTime

enum class EventType {
    GAME_STARTED,
    GAME_ENDED,
    GAME_HAS_WINNER,
    GAME_CHANGE_TURN_PLAYER,
    SCORE_LIST_ANNOUNCED,
    DEMO_ZONE_CHANGED,
    PLAYER_DID_KEEP,
    PLAYER_DID_DROP
}

class Event(val type: EventType) {
    val createAt: LocalDateTime = LocalDateTime.now()
    var playerId: String? = null
    var data: String? = null

    constructor(type: EventType, playerId: String) : this(type) {
        this.playerId = playerId
    }

    constructor(type: EventType, playerId: String, data: String) : this(type, playerId) {
        this.data = data
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (type != other.type) return false
        if (playerId != other.playerId) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (playerId?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Event(type=$type, playerId=$playerId, data=$data)"
    }

}

class Events {
    companion object {
        fun winner(playerId: String): Event {
            return Event(EventType.GAME_HAS_WINNER, playerId)
        }

        fun scoreList(scoreList: String): Event {
            val event = Event(EventType.SCORE_LIST_ANNOUNCED)
            event.data = scoreList
            return event
        }

        fun turnPlayer(playerId: String): Event {
            return Event(EventType.GAME_CHANGE_TURN_PLAYER, playerId)
        }

        fun demoZone(demoZoneState: String): Event {
            val event = Event(EventType.DEMO_ZONE_CHANGED)
            event.data = demoZoneState
            return event
        }

        fun playerKeepCard(playerId: String, card: Card): Event {
            return Event(EventType.PLAYER_DID_KEEP, playerId, card.toCompatExpr())
        }

        fun playerDropCard(playerId: String, cardIndex: Int): Event {
            return Event(EventType.PLAYER_DID_DROP, playerId, "$cardIndex")
        }

        val GAME_STARTED = Event(EventType.GAME_STARTED)
        val GAME_ENDED = Event(EventType.GAME_ENDED)
    }

}