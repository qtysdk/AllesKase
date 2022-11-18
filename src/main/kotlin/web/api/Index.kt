package web

import gaas.usecases.CreateGameUseCase
import gaas.usecases.QueryGameStatus
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
class GameStatusEvent {

}

@Serializable
data class GamePlayer(val playerId: String, val alive: Boolean, val keptCards: String)

@Serializable
data class GameTurn(val playerId: String, val diceValue: Int, val actionList: String, val actionIndex: String)

@Serializable
class GameStatusResponse {
    val players = mutableListOf<GamePlayer>()
    val events = mutableListOf<GameStatusEvent>()
    var turn: GameTurn? = null
    var demoZone = ""

    fun addPlayer(playerId: String, alive: Boolean, cards: String) {
        players.add(GamePlayer(playerId, alive, cards))
    }

    fun turn(playerId: String, diceValue: Int, actionList: String, actionIndex: String) {
        turn = GameTurn(playerId, diceValue, actionList, actionIndex)
    }

}

fun Application.configureAPIs() {

    install(ContentNegotiation) {
        json()
    }

    val createGameUseCase by inject<CreateGameUseCase>()
    val queryGameStatus by inject<QueryGameStatus>()

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

            val response = GameStatusResponse()
            queryGameStatus.query(gameId!!, response)
            call.respond(response)

        }
    }
}
