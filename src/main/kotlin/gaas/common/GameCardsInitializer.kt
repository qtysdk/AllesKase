package gaas.common

import gaas.domain.Game

interface GameInitializer {
    fun resetCards(game: Game)
}

class DefaultGameInitializer : GameInitializer {
    override fun resetCards(game: Game) {
        game.providingDeck.resetForProvidingDeck()
        (1..6).forEach {
            game.demoZone.add(game.providingDeck.deal())
        }
    }
}