package web

import gaas.common.IllegalGameStateException
import gaas.repository.Database
import gaas.usecases.CreateGameUseCase
import gaas.usecases.CreateGameUseCaseImpl
import gaas.usecases.GetGameViewUseCase
import gaas.usecases.GetGameViewUseCaseImpl
import gaas.usecases.JoinGameUseCase
import gaas.usecases.JoinGameUseCaseImpl
import gaas.usecases.StartGameUseCase
import gaas.usecases.StartGameUseCaseImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.koin.core.KoinApplication
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalGameStateException> { call, throwable ->
            when (throwable) {
                is IllegalGameStateException -> {
                    call.respond(
                        HttpStatusCode.BadRequest, "${throwable.message}"
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
    })
}

