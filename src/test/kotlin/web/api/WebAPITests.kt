package web.api

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import web.configureAPIs
import kotlin.test.Test
import kotlin.test.assertEquals

class WebAPITests {
    @Test
    fun testHelloWorld() = testApplication {
        application {
            configureAPIs()
        }

        val response = client.get("/")
        assertEquals(
            """Welcome to GaaS :: Alles Kase Game (玩命起司)""",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}