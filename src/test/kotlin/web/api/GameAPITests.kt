package web.api

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
        assertEquals(
            """{"players":[{"playerId":"fake-player-1","alive":true,"keptCards":""}],"events":[],"turn":null,"demoZone":""}""",
            gameStatusResponse.bodyAsText()
        )
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