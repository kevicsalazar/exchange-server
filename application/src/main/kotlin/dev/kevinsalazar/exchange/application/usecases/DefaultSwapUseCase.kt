package dev.kevinsalazar.exchange.application.usecases

import dev.kevinsalazar.exchange.application.utils.generateUUID
import dev.kevinsalazar.exchange.domain.entities.Balance
import dev.kevinsalazar.exchange.domain.entities.Transaction
import dev.kevinsalazar.exchange.domain.enums.Status
import dev.kevinsalazar.exchange.domain.errors.InsufficientFundsException
import dev.kevinsalazar.exchange.domain.payload.request.SwapRequest
import dev.kevinsalazar.exchange.domain.ports.driven.BalanceRepository
import dev.kevinsalazar.exchange.domain.ports.driven.TransactionRepository
import dev.kevinsalazar.exchange.domain.ports.driving.SwapUseCase
import dev.kevinsalazar.exchange.domain.utils.getTimeStamp

internal class DefaultSwapUseCase(
    private val balanceRepository: BalanceRepository,
    private val transactionRepository: TransactionRepository
) : SwapUseCase {

    override suspend fun execute(userId: String, request: SwapRequest): Result<Transaction> {

        val transaction = Transaction(
            id = generateUUID(),
            userId = userId,
            status = Status.Success,
            sentCurrencyId = request.send.currencyId,
            sentAmount = request.send.amount,
            receivedCurrencyId = request.receive.currencyId,
            receivedAmount = request.receive.amount,
            created = getTimeStamp()
        )

        try {
            val senderBalance = balanceRepository.findBalance(userId, request.send.currencyId)

            if (senderBalance != null && senderBalance.amount >= request.send.amount) {

                val senderNewBalance = senderBalance.copy(
                    amount = senderBalance.amount - request.send.amount
                )

                balanceRepository.updateBalance(senderNewBalance)

                val recipientBalance = balanceRepository.findBalance(userId, request.receive.currencyId)

                val recipientNewBalance = recipientBalance?.let {
                    it.copy(
                        amount = it.amount + request.receive.amount
                    )
                } ?: Balance(
                    id = generateUUID(),
                    userId = userId,
                    amount = request.receive.amount,
                    currencyId = request.receive.currencyId
                )

                balanceRepository.updateBalance(recipientNewBalance)

                val savedTransaction = transactionRepository.save(transaction)

                requireNotNull(savedTransaction) { "Unable to save transaction" }

                return Result.success(transaction)
            } else {
                throw InsufficientFundsException()
            }
        } catch (e: Exception) {
            transactionRepository.save(
                transaction.copy(
                    status = Status.Error
                )
            )
            return Result.failure(e)
        }
    }

}