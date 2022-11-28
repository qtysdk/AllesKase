package web

import gaas.usecases.CreateGameUseCase
import gaas.usecases.GetGameViewUseCase
import gaas.usecases.JoinGameUseCase
import gaas.usecases.PlayerActionUseCase
import gaas.usecases.QueryAvailableGameIds
import gaas.usecases.StartGameUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class CreateGameRequest(val playerId: String)

@Serializable
data class QueryAvailableGameIdsResponse(val gameIds: List<String>)

@Serializable
data class CreateGameResponse(val gameId: String)

@Serializable
data class JoinGameResponse(val isSuccess: Boolean)

@Serializable
data class PlayActionRequest(val action: String, val index: Int)

fun Application.configureAPIs() {

    install(ContentNegotiation) {
        json()
    }

    val createGameUseCase by inject<CreateGameUseCase>()
    val queryAvailableGameIds by inject<QueryAvailableGameIds>()
    val getGameViewUseCase by inject<GetGameViewUseCase>()
    val joinGameUseCase by inject<JoinGameUseCase>()
    val startGameUseCase by inject<StartGameUseCase>()
    val playerActionUseCase by inject<PlayerActionUseCase>()

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

        // get available game ids
        get("/games") {
            call.respond(QueryAvailableGameIdsResponse(queryAvailableGameIds.query()))
        }

        // query game status
        get("/games/{gameId}/player/{playerId}/status") {
            val (gameId, playerId) = getGameAndPlayer()
            call.respond(getGameViewUseCase.fetch(gameId, playerId))
        }

        // join game
        post("/games/{gameId}/player/{playerId}/join") {
            val (gameId, playerId) = getGameAndPlayer()
            call.respond(JoinGameResponse(joinGameUseCase.join(gameId, playerId)))
        }

        // start game
        post("/games/{gameId}/player/{playerId}/start") {
            val (gameId, playerId) = getGameAndPlayer()
            startGameUseCase.start(gameId, playerId)
            call.respond(HttpStatusCode.Accepted)
        }

        // player do action
        post("/games/{gameId}/player/{playerId}/act") {
            val playActionRequest = call.receive<PlayActionRequest>()
            val (gameId, playerId) = getGameAndPlayer()
            playerActionUseCase.doAction(gameId, playerId, playActionRequest.action, playActionRequest.index)
            call.respond(HttpStatusCode.Accepted)
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getGameAndPlayer(): Pair<String, String> {
    val gameId = call.parameters["gameId"]
    val playerId = call.parameters["playerId"]
    if (gameId == null || playerId == null) {
        call.respond(HttpStatusCode.BadRequest)
    }
    return Pair(gameId!!, playerId!!)
}
