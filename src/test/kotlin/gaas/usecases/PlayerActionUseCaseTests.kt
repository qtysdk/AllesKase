package gaas.usecases

import gaas.common.GameInitializer
import gaas.domain.Card
import gaas.domain.CardType
import gaas.domain.Dice
import gaas.domain.Game
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlayerActionUseCaseTests : BaseEndToEndTests() {

    private val playerActionUseCase: PlayerActionUseCase = PlayerActionUseCaseImpl(database)

    @Test
    internal fun test_dice_value_not_matching_any_cards() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)

        givenPresetDemoZone_1C2C3C1C5C2C_ProvidingDeck_6T6T_WithDiceValue(gameId, 4)
        whenStartTheGame(gameId, PLAYER_1)

        thenTurnPlayerWillBe(gameId, PLAYER_1)
        thenActionCanDoWithPositions(gameId, listOf("PEEP"), listOf(0, 1, 2, 3, 4, 5))

        // when player do "peep" to index  2 will get 3C
        playerActionUseCase.doAction(gameId, PLAYER_1, "PEEP", 2)

        // then player see the 3C in the private message
        assertEquals(database.findPlayerById(PLAYER_1)!!.privateMessages()[0], "peep, index:2, card: 3C")
    }

    @Test
    internal fun test_dice_value_matching_some_cards_and_player_do_KEEP() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)

        givenPresetDemoZone_1C2C3C1C5C2C_ProvidingDeck_6T6T_WithDiceValue(gameId, 2)
        whenStartTheGame(gameId, PLAYER_1)

        thenTurnPlayerWillBe(gameId, PLAYER_1)
        thenActionCanDoWithPositions(gameId, listOf("KEEP", "DROP"), listOf(1, 5))

        // when player do "keep" index 1 will have 2C in their own deck
        playerActionUseCase.doAction(gameId, PLAYER_1, "KEEP", 1)

        // then player got the 2C
        val keptCard = database.findPlayerById(PLAYER_1)!!.keptCards[0]
        assertEquals(2, keptCard.value)
        assertEquals(CardType.CHEESE, keptCard.type)

        // then demo-zone refile 6T at index 1
        val refilledCard = database.findGameById(gameId)!!.demoZone.cards[1]
        assertEquals(6, refilledCard.value)
        assertEquals(CardType.TRAP, refilledCard.type)
    }

    @Test
    internal fun test_dice_value_matching_some_cards_and_player_do_DROP() {
        givenPlayerWithId(PLAYER_1)
        givenPlayerWithId(PLAYER_2)
        val gameId = givenGameWithPlayers(PLAYER_1, PLAYER_2)

        givenPresetDemoZone_1C2C3C1C5C2C_ProvidingDeck_6T6T_WithDiceValue(gameId, 2)
        whenStartTheGame(gameId, PLAYER_1)

        thenTurnPlayerWillBe(gameId, PLAYER_1)
        thenActionCanDoWithPositions(gameId, listOf("KEEP", "DROP"), listOf(1, 5))

        // when player do "keep" index 1 will have 2C in their own deck
        playerActionUseCase.doAction(gameId, PLAYER_1, "DROP", 1)

        // then found the 2C at dropped deck
        val keptCard = database.findGameById(gameId)!!.droppedDeck.cards[0]
        assertEquals(2, keptCard.value)
        assertEquals(CardType.CHEESE, keptCard.type)

        // then demo-zone refile 6T at index 1
        val refilledCard = database.findGameById(gameId)!!.demoZone.cards[1]
        assertEquals(6, refilledCard.value)
        assertEquals(CardType.TRAP, refilledCard.type)
    }


    private fun thenTurnPlayerWillBe(gameId: String, playerId: String) {
        assertEquals(queryGameStatus.query(gameId).turn.player.id, playerId)
    }

    private fun thenActionCanDoWithPositions(gameId: String, actions: List<String>, actionablePosition: List<Int>) {
        var s = queryGameStatus.query(gameId)
        assertEquals(actions, s.turn.actionList)
        assertEquals(actionablePosition, s.turn.actionIndex)
    }

    private fun givenPresetDemoZone_1C2C3C1C5C2C_ProvidingDeck_6T6T_WithDiceValue(gameId: String, diceValue: Int) {
        val game = database.findGameById(gameId)!!
        game.cardsInitializer = object : GameInitializer {
            override fun resetCards(game: Game) {
                // give 2 available cards in the providing deck
                game.providingDeck.cards.clear()
                game.providingDeck.cards.add(Card(6, CardType.TRAP))
                game.providingDeck.cards.add(Card(6, CardType.TRAP))


                // set demoZone cards all 1 point cheese
                game.demoZone.cards.clear()

                game.demoZone.add(Card(1, CardType.CHEESE))
                game.demoZone.add(Card(2, CardType.CHEESE))
                game.demoZone.add(Card(3, CardType.CHEESE))
                game.demoZone.add(Card(1, CardType.CHEESE))
                game.demoZone.add(Card(5, CardType.CHEESE))
                game.demoZone.add(Card(2, CardType.CHEESE))


                // make sure the dice always get $diceValue
                game.dice = mockk<Dice>()
                every { game.dice.roll() } returns diceValue
            }
        }
    }


}