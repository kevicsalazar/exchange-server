package dev.kevinsalazar.exchange.infraestructure.persist.repository

import dev.kevinsalazar.exchange.infraestructure.persist.tables.TransactionTable
import dev.kevinsalazar.exchange.infraestructure.persist.utils.dbQuery
import dev.kevinsalazar.exchange.domain.entities.Transaction
import dev.kevinsalazar.exchange.domain.ports.driven.TransactionRepository
import dev.kevinsalazar.exchange.domain.utils.instantToString
import dev.kevinsalazar.exchange.domain.utils.stringToInstant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert

internal class DefaultTransactionRepository : TransactionRepository {

    override suspend fun save(transaction: Transaction): Transaction? {
        return dbQuery {
            TransactionTable.insert {
                it[id] = transaction.id
                it[userId] = transaction.userId
                it[status] = transaction.status
                it[sentCurrencyId] = transaction.sentCurrencyId
                it[sentAmount] = transaction.sentAmount
                it[receivedCurrencyId] = transaction.receivedCurrencyId
                it[receivedAmount] = transaction.receivedAmount
                it[created] = stringToInstant(transaction.created)
            }.resultedValues
                ?.firstOrNull()
                ?.let(::rowToTransaction)
        }
    }

    private fun rowToTransaction(row: ResultRow): Transaction {
        return Transaction(
            id = row[TransactionTable.id],
            userId = row[TransactionTable.userId],
            status = row[TransactionTable.status],
            sentCurrencyId = row[TransactionTable.sentCurrencyId],
            sentAmount = row[TransactionTable.sentAmount],
            receivedCurrencyId = row[TransactionTable.receivedCurrencyId],
            receivedAmount = row[TransactionTable.receivedAmount],
            created = instantToString(row[TransactionTable.created])
        )
    }
}