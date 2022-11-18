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
import web.configureAPIs
import web.initKoinModules
import kotlin.test.Test
import kotlin.test.assertEquals

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

    private fun ApplicationTestBuilder.initTestApp() {
        install(Koin) {
            initKoinModules()
        }

        application {
            configureAPIs()
        }
    }
}