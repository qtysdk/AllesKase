package web

import gaas.repository.Database
import gaas.usecases.CreateGameUseCase
import gaas.usecases.CreateGameUseCaseImpl
import gaas.usecases.GetGameViewUseCase
import gaas.usecases.GetGameViewUseCaseImpl
import gaas.usecases.JoinGameUseCase
import gaas.usecases.JoinGameUseCaseImpl
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.KoinApplication
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        initKoinModules()
    }

    configureAPIs()

}

fun KoinApplication.initKoinModules() {
    modules(module {
        single { Database() }
        single<CreateGameUseCase> { CreateGameUseCaseImpl(get()) }
        single<GetGameViewUseCase> { GetGameViewUseCaseImpl(get()) }
        single<JoinGameUseCase> { JoinGameUseCaseImpl(get()) }
    })
}

