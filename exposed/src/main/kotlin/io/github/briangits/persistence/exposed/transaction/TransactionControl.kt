package io.github.briangits.persistence.exposed.transaction

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.InternalApi
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.withTransactionContext

class TransactionControl(
    private val transaction: JdbcTransaction,
    private val dispatcher: CoroutineDispatcher
) {
    @OptIn(InternalApi::class)
    suspend fun <T> execute(block: suspend () -> T): T =
        withContext(dispatcher) {
            withTransactionContext(transaction) {
                try {
                    block()
                } catch (e: Throwable) {
                    runCatching { transaction.rollback() }
                        .onFailure { e.addSuppressed(it) }
                    throw e
                }
            }
        }

    suspend fun commit() = withContext(dispatcher) {
        try {
            transaction.commit()
        } finally {
            transaction.close()
        }
    }

    suspend fun rollback() = withContext(dispatcher) {
        try {
            transaction.rollback()
        } finally {
            transaction.close()
        }
    }
}
