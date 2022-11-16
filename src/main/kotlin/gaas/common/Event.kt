package gaas.common

import gaas.domain.Card
import java.time.LocalDateTime

class Event(val message: String) {
    val createAt: LocalDateTime = LocalDateTime.now()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }

    override fun toString(): String {
        return "Event(message='$message')"
    }


}

class Events {
    companion object {
        fun winner(playerId: String): Event {
            return Event("$playerId won")
        }

        fun scoreList(scoreList: String): Event {
            return Event(scoreList)
        }

        fun turnPlayer(playerId: String): Event {
            return Event("turn-player: $playerId")
        }

        fun demoZone(demoZoneState: String): Event {
            return Event(demoZoneState)
        }

        fun playerKeepCard(playerId: String, card: Card): Event {
            return Event("player[$playerId], keep-card: $card")
        }

        fun playerDropCard(playerId: String, cardIndex: Int): Event {
            return Event("player[$playerId], drop-card: $cardIndex")
        }

        val GAME_STARTED = Event("game has stated")
        val GAME_ENDED = Event("game has ended")
    }

}