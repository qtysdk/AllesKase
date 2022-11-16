package gaas.domain

import org.junit.jupiter.api.Test

class DeckTest {

    @Test
    internal fun test_reset_for_providing_deck() {
        // given a new deck
        val deck = Deck()

        // when reset it for providing deck
        deck.resetForProvidingDeck()

        // then should get 36 cards matching the rules
        deck.selfValidateForProvidingDeck()
    }
}