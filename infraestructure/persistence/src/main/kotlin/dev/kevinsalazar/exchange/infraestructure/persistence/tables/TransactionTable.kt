package dev.kevinsalazar.exchange.infraestructure.persistence.tables

import dev.kevinsalazar.exchange.domain.enums.Status
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object TransactionTable : Table(name = "transactions") {
    val id = varchar("id", length = 36)
    val userId = reference("user_id", UsersTable.id)
    val status = enumeration<Status>("status")
    val sentCurrencyCode = reference("sent_currency_code", CurrencyTable.code).nullable()
    val sentAmount = float("sent_amount").nullable()
    val receivedCurrencyCode = reference("sent_currency_code", CurrencyTable.code).nullable()
    val receivedAmount = float("received_amount").nullable()
    val created = timestamp("timestamp")

    override val primaryKey = PrimaryKey(id)
}