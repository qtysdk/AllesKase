package web

import gaas.usecases.CreateGameUseCase
import gaas.usecases.GetGameViewUseCase
import gaas.usecases.JoinGameUseCase
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class CreateGameRequest(val playerId: String)


@Serializable
data class CreateGameResponse(val gameId: String)

@Serializable
data class JoinGameResponse(val isSuccess: Boolean)


fun Application.configureAPIs() {

    install(ContentNegotiation) {
        json()
    }

    val createGameUseCase by inject<CreateGameUseCase>()
    val getGameViewUseCase by inject<GetGameViewUseCase>()
    val joinGameUseCase by inject<JoinGameUseCase>()

    routing {
        get("/") {
            call.respondText { "Welcome to GaaS :: Alles Kase Game (玩命起司)" }
        }
    }

    // Game
    routing {
        // create game
        post("/games") {
            val request = call.receive<CreateGameRequest>()
            call.respond(CreateGameResponse(createGameUseCase.create(request.playerId)))
        }

        // query game status
        get("/games/{gameId}/status") {
            val gameId = call.parameters["gameId"]
            if (gameId == null) {
                call.respond(HttpStatusCode.BadRequest)
            }

            call.respond(getGameViewUseCase.fetch(gameId!!))
        }

        post("/games/{gameId}/player/{playerId}/join") {
            val gameId = call.parameters["gameId"]
            val playerId = call.parameters["playerId"]
            if (gameId == null || playerId == null) {
                call.respond(HttpStatusCode.BadRequest)
            }
            call.respond(JoinGameResponse(joinGameUseCase.join(gameId!!, playerId!!)))
        }
    }
}
