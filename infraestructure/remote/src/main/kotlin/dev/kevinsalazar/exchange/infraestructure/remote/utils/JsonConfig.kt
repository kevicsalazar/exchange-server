package dev.kevinsalazar.exchange.infraestructure.remote.utils

import kotlinx.serialization.json.Json

val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}