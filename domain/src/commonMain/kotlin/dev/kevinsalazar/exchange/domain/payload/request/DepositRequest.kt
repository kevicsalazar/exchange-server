package dev.kevinsalazar.exchange.domain.payload.request

import kotlinx.serialization.Serializable

@Serializable
data class DepositRequest(
    val currencyCode: String,
    val amount: Float
)