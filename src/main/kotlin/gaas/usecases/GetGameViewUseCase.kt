package gaas.usecases

import gaas.common.Event
import gaas.domain.Deck
import gaas.domain.DemoZone
import gaas.domain.Game
import gaas.domain.Player
import gaas.domain.Turn
import gaas.ports.CardActions
import gaas.ports.DeckTopCard
import gaas.ports.EventOutput
import gaas.ports.GameViewOutput
import gaas.ports.PlayerOutput
import gaas.ports.TurnOutput
import gaas.repository.Database
import java.time.format.DateTimeFormatter

interface GetGameViewUseCase {
    fun fetch(gameId: String, playerId: String): GameViewOutput

}

class GetGameViewUseCaseImpl(private val database: Database) : GetGameViewUseCase {
    override fun fetch(gameId: String, playerId: String): GameViewOutput {
        val game = database.findGameById(gameId)!!
        val player = database.findPlayerById(playerId)!!
        val isTurnPlayer = game.turn.player.id == playerId
        return GameViewOutput(
            gameId,
            game.players.map { p ->
                PlayerOutput(
                    p.id,
                    p.toCompatCardsExpression(),
                    p.scores(),
                    p.alive
                )
            }.toList(),
            game.turn.let {
                TurnOutput(
                    PlayerOutput(
                        game.turn.player.id,
                        game.turn.player.toCompatCardsExpression(),
                        game.turn.player.scores(),
                        game.turn.player.alive
                    ),
                    game.turn.diceValue,
                    game.turn.actionList.map { action -> action.name }.toList(),
                    game.turn.actionIndex.toList()
                )
            }, toDemoZoneOutput(isTurnPlayer, game.turn, game.demoZone),
            toDeckTopView(game.providingDeck),
            toDeckTopView(game.droppedDeck),
            toEvents(game, player)
        )

    }

    private fun toDemoZoneOutput(isTurnPlayer: Boolean, turn: Turn, demoZone: DemoZone): List<CardActions> {
        if (!isTurnPlayer) {
            return demoZone.toCardValues().mapIndexed { index, value ->
                CardActions(
                    index, value, emptyList()
                )
            }.toList()
        }

        // no dice value matched, PEEP only
        if (!demoZone.toCardValues().contains(turn.diceValue)) {
            return demoZone.toCardValues().mapIndexed { index, value ->
                CardActions(
                    index, value,
                    turn.actionList.map { action -> action.name }.toList()
                )
            }.toList()
        }

        return demoZone.toCardValues().mapIndexed { index, value ->
            CardActions(
                index, value, if (value == turn.diceValue) {
                    turn.actionList.map { action -> action.name }.toList()
                } else {
                    emptyList()
                }
            )
        }.toList()
    }

    private fun toEvents(game: Game, player: Player): List<EventOutput> {
        val mergedEvents = mutableListOf<Event>()
        mergedEvents.addAll(game.events)
        mergedEvents.addAll(player.events())
        mergedEvents.sortBy { e -> e.createAt }
        return mergedEvents.map { it ->
            EventOutput(
                it.type.name,
                it.createAt.format(DateTimeFormatter.ISO_DATE_TIME),
                it.data ?: "",
                it.playerId
            )
        }.toList()
    }

    private fun toDeckTopView(deck: Deck): DeckTopCard {
        if (deck.isEmpty()) {
            return DeckTopCard(0, 0)
        }
        return DeckTopCard(deck.last().value, deck.size())
    }

}
