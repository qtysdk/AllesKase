package gaas.usecases

import gaas.domain.Deck
import gaas.ports.DeckTopCard
import gaas.ports.GameViewOutput
import gaas.ports.PlayerOutput
import gaas.ports.TurnOutput
import gaas.repository.Database

interface GetGameViewUseCase {
    fun fetch(gameId: String, playerId: String): GameViewOutput
}

class GetGameViewUseCaseImpl(private val database: Database) : GetGameViewUseCase {
    override fun fetch(gameId: String, playerId: String): GameViewOutput {
        val game = database.findGameById(gameId)!!
        return GameViewOutput(
            gameId,
            game.players.map { player ->
                PlayerOutput(
                    player.id,
                    player.toCompatCardsExpression(),
                    player.scores(),
                    player.alive
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
            }, game.demoZone.toCardValues(),
            toDeckTopView(game.providingDeck),
            toDeckTopView(game.droppedDeck)
        )

    }

    private fun toDeckTopView(deck: Deck): DeckTopCard {
        if (deck.isEmpty()) {
            return DeckTopCard(0, 0)
        }
        return DeckTopCard(deck.last().value, deck.size())
    }

}
