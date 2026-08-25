package io.github.briangits.persistence.exposed.transaction

import org.jetbrains.exposed.v1.core.InternalApi
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.withTransactionContext

class TransactionControl(private val transaction: JdbcTransaction) {
    @OptIn(InternalApi::class)
    suspend fun <T> execute(block: suspend () -> T): T =
        withTransactionContext(transaction) {
            try {
                block()
            } catch (e: Throwable) {
                runCatching { transaction.rollback() }
                    .onFailure { e.addSuppressed(it) }
                throw e
            }
        }

    suspend fun commit() {
        try {
            transaction.commit()
        } finally {
            transaction.close()
        }
    }

    suspend fun rollback() {
        try {
            transaction.rollback()
        } finally {
            transaction.close()
        }
    }
}
