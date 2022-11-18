package gaas.usecases

import gaas.ports.DemoZoneOutput
import gaas.ports.GameViewOutput
import gaas.ports.PlayerOutput
import gaas.ports.TurnOutput
import gaas.repository.Database

interface GetGameViewUseCase {
    fun fetch(gameId: String): GameViewOutput
}

class GetGameViewUseCaseImpl(private val database: Database) : GetGameViewUseCase {
    override fun fetch(gameId: String): GameViewOutput {
        val game = database.findGameById(gameId)!!
        return GameViewOutput(
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
            }, DemoZoneOutput(game.demoZone.toCompatCardsExpression())
        )

    }


}
