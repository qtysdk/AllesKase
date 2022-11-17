package web

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureAPIs() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
