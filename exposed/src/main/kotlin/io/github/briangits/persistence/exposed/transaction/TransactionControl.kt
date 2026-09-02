package io.github.briangits.persistence.exposed.transaction

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.InternalApi
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.withTransactionContext

/**
 * Orchestrates the execution of database operations within a specific [JdbcTransaction].
 *
 * This class ensures that operations are executed within the correct transactional context
 * and handles automatic rollback on failures, as well as final commitment/rollback and
 * connection closure.
 *
 * @property transaction The underlying Exposed [JdbcTransaction].
 * @property dispatcher The [CoroutineDispatcher] on which database operations are executed.
 */
class TransactionControl(
    private val transaction: JdbcTransaction,
    private val dispatcher: CoroutineDispatcher
) {
    /**
     * Executes the given [block] within the transaction context.
     *
     * If the block fails, the transaction is automatically rolled back before rethrowing
     * the exception.
     *
     * @param T The return type of the block.
     * @param block The suspending block to execute.
     * @return The result of the block.
     */
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

    /**
     * Commits the transaction and closes the underlying connection.
     */
    suspend fun commit() = withContext(dispatcher) {
        try {
            transaction.commit()
        } finally {
            transaction.close()
        }
    }

    /**
     * Rolls back the transaction and closes the underlying connection.
     */
    suspend fun rollback() = withContext(dispatcher) {
        try {
            transaction.rollback()
        } finally {
            transaction.close()
        }
    }
}
