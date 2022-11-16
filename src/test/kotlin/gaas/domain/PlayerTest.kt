package gaas.domain

import gaas.common.IllegalGameStateException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PlayerTest {
    @Test
    internal fun player_will_die_with_3_traps() {
        val player = Player("player-id")

        // keep 1 trap will be still alive
        player.keepCard(Card(1, CardType.TRAP))
        assertTrue(player.alive)

        // keep 2 traps will be still alive
        player.keepCard(Card(1, CardType.TRAP))
        assertTrue(player.alive)

        // keep 3 traps will be dead
        player.keepCard(Card(1, CardType.TRAP))
        assertFalse(player.alive)

        val exception = assertFailsWith<IllegalGameStateException> {
            player.keepCard(Card(1, CardType.TRAP))
        }
        assertEquals("PLAYER_HAS_BEEN_DEAD", exception.message)
    }

    @Test
    internal fun test_scoring() {
        val player = Player("player-id")
        player.keepCard(Card(1, CardType.TRAP))

        // got 0, because traps has no points
        assertEquals(0, player.scores())

        // got 6
        player.keepCard(Card(6, CardType.CHEESE))
        assertEquals(6, player.scores())

        // got 11 (5 + 6 + 0)
        player.keepCard(Card(5, CardType.CHEESE))
        assertEquals(11, player.scores())

        // got 12
        player.keepCard(Card(1, CardType.TRAP))
        player.keepCard(Card(1, CardType.CHEESE))
        assertEquals(12, player.scores())

        // got 0, player dead
        player.keepCard(Card(1, CardType.TRAP))
        assertEquals(0, player.scores())
    }
}