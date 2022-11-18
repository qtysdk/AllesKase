package web.api

import gaas.ports.GameViewOutput
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import web.CreateGameRequest
import web.CreateGameResponse
import web.JoinGameResponse
import web.configureAPIs
import web.configureStatusPages
import web.initKoinModules
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameAPITests {
    @Test
    fun testCreateGame() = testApplication {
        initTestApp()

        val response = client.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateGameRequest("fake-player-1")))
        }
        val createGameResponse = Json.decodeFromString<CreateGameResponse>(response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)


        val gameStatusResponse = client.get("/games/${createGameResponse.gameId}/status")
        assertEquals(HttpStatusCode.OK, gameStatusResponse.status)

        val gameViewOutput = Json.decodeFromString<GameViewOutput>(gameStatusResponse.bodyAsText())

        // only the game host in the game
        assertEquals(1, gameViewOutput.players.size)

        // empty player with id: -
        assertEquals("-", gameViewOutput.turn.player.playerId)

        // empty demo-zone before the game started
        assertEquals("", gameViewOutput.demoZone.cards)
    }

    @Test
    fun testJoinGameSuccessfully() = testApplication {
        initTestApp()

        val gameId = givenCreatedGame()
        val playerId = "fake-player-2"

        val joinGameResponse = Json.decodeFromString<JoinGameResponse>(
            client.post("/games/$gameId/player/$playerId/join").bodyAsText()
        )
        assertEquals(true, joinGameResponse.isSuccess)
    }

    @Test
    fun testStartGame() = testApplication {
        initTestApp()

        val gameId = givenCreatedGame()

        val failedResponse = client.post("/games/$gameId/player/fake-player-1/start")
        assertEquals(HttpStatusCode.BadRequest, failedResponse.status)
        assertEquals("TOO_FEW_PLAYERS", failedResponse.bodyAsText())


        // given the second player
        assertTrue(
            Json.decodeFromString<JoinGameResponse>(
                client.post("/games/$gameId/player/fake-player-2/join").bodyAsText()
            ).isSuccess
        )

        val gameStatusResponse = client.get("/games/$gameId/status")
        assertEquals(HttpStatusCode.OK, gameStatusResponse.status)
        println(gameStatusResponse.bodyAsText())

        // then start it again will accepted
        val response = client.post("/games/$gameId/player/fake-player-1/start")
        assertEquals(HttpStatusCode.Accepted, response.status)
    }

    private suspend fun ApplicationTestBuilder.givenCreatedGame(): String {
        val response = client.post("/games") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateGameRequest("fake-player-1")))
        }
        val gameId = Json.decodeFromString<CreateGameResponse>(response.bodyAsText()).gameId
        return gameId
    }


    private fun ApplicationTestBuilder.initTestApp() {
        install(Koin) {
            initKoinModules()
        }

        application {
            configureStatusPages()
            configureAPIs()
        }
    }
}