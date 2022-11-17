package web.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
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
            """{"hello":"world"}""",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}