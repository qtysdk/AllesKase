package gaas.domain

import org.junit.jupiter.api.Test
import kotlin.coroutines.suspendCoroutine
import kotlin.test.assertEquals

class DeckTest {

    @Test
    internal fun test_reset_for_providing_deck() {
        // given a new deck
        val deck = Deck()

        // when reset it for providing deck
        deck.resetForProvidingDeck()

        // then should get 36 cards matching the rules
        assertEquals(36, deck.size())

        assertEquals(6, deck.cards.filter { it.value == 1 }.size)
        assertEquals(4, deck.cards.filter { it.value == 1 && it.type == CardType.CHEESE }.size)

        assertEquals(6, deck.cards.filter { it.value == 2 }.size)
        assertEquals(4, deck.cards.filter { it.value == 2 && it.type == CardType.CHEESE }.size)

        assertEquals(6, deck.cards.filter { it.value == 3 }.size)
        assertEquals(3, deck.cards.filter { it.value == 3 && it.type == CardType.CHEESE }.size)

        assertEquals(6, deck.cards.filter { it.value == 4 }.size)
        assertEquals(3, deck.cards.filter { it.value == 4 && it.type == CardType.CHEESE }.size)

        assertEquals(6, deck.cards.filter { it.value == 5 }.size)
        assertEquals(2, deck.cards.filter { it.value == 5 && it.type == CardType.CHEESE }.size)

        assertEquals(6, deck.cards.filter { it.value == 6 }.size)
        assertEquals(2, deck.cards.filter { it.value == 6 && it.type == CardType.CHEESE }.size)

    }
}