package web

import gaas.common.IllegalGameStateException
import gaas.common.IllegalPlayerActionException
import gaas.repository.Database
import gaas.usecases.CreateGameUseCase
import gaas.usecases.CreateGameUseCaseImpl
import gaas.usecases.GetGameViewUseCase
import gaas.usecases.GetGameViewUseCaseImpl
import gaas.usecases.JoinGameUseCase
import gaas.usecases.JoinGameUseCaseImpl
import gaas.usecases.PlayerActionUseCase
import gaas.usecases.PlayerActionUseCaseImpl
import gaas.usecases.StartGameUseCase
import gaas.usecases.StartGameUseCaseImpl
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@Serializable
data class ErrorMessage(val code: Int, val message: String)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, throwable ->
            when (throwable) {
                is IllegalGameStateException, is IllegalPlayerActionException -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorMessage(HttpStatusCode.BadRequest.value, throwable.message!!)
                    )
                }

                else -> {
                    throwable.printStackTrace()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorMessage(HttpStatusCode.InternalServerError.value, throwable.toString())
                    )
                }
            }
        }
    }
}

fun Application.module() {
    install(Koin) {
        initKoinModules()
    }
    install(CORS) {
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)

        // TODO the origin should be configurable
//        allowOrigins { it == "http://localhost:3000/" }
        anyHost()
    }

    configureStatusPages()
    configureAPIs()

}

fun KoinApplication.initKoinModules() {
    modules(module {
        single { Database() }
        single<CreateGameUseCase> { CreateGameUseCaseImpl(get()) }
        single<GetGameViewUseCase> { GetGameViewUseCaseImpl(get()) }
        single<JoinGameUseCase> { JoinGameUseCaseImpl(get()) }
        single<StartGameUseCase> { StartGameUseCaseImpl(get()) }
        single<PlayerActionUseCase> { PlayerActionUseCaseImpl(get()) }
    })
}

