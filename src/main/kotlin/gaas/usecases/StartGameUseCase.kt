package gaas.usecases

import gaas.repository.Database

interface StartGameUseCase {
    abstract fun start(gameId: String, playerId: String)

}

class StartGameUseCaseImpl(private val database: Database) : StartGameUseCase {
    override fun start(gameId: String, playerId: String) {
        // TODO check player own this game
        // send started event
        val game = database.findGameById(gameId)
        game.addEvent("game has stated")

        // check shall we end the game?
        // 1. only 1 player alive
        // 2. more than 1 player alive, but not more cards in the providerDeck
        if (game.canEndByOnlyOnePlayerAlive()) {
            val alivePlayer = game.players.filter { p -> p.alive }.take(1).last()
            game.addEvent("game has ended")
            game.addEvent("${alivePlayer.id} won")
            return
        }

        // TODO case 2

        // otherwise, make the first player actionable
        game.addEvent("player-? act")
        return
    }

}

