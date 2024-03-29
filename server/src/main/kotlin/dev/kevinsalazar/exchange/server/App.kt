package dev.kevinsalazar.exchange.server

import dev.kevinsalazar.exchange.infraestructure.api.apiModule
import dev.kevinsalazar.exchange.infraestructure.api.config.configureAuthentication
import dev.kevinsalazar.exchange.infraestructure.api.config.configureRoutes
import dev.kevinsalazar.exchange.infraestructure.persistence.persistModule
import dev.kevinsalazar.exchange.infraestructure.remote.remoteModule
import dev.kevinsalazar.exchange.application.applicationModule
import dev.kevinsalazar.exchange.infraestructure.api.config.configureEventConsumer
import dev.kevinsalazar.exchange.infraestructure.events.eventsModule
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    return EngineMain.main(args)
}

fun Application.main() {
    configureKoin()
    configureAuthentication()
    configureEventConsumer()
    configureRoutes()
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger(level = Level.INFO)
        modules(
            appModule,
            apiModule,
            eventsModule,
            persistModule,
            remoteModule,
            applicationModule,
        )
    }
}

val Application.appModule
    get() = module {
        single { environment }
    }