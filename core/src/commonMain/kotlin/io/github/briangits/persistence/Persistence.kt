package io.github.briangits.persistence

import io.github.briangits.persistence.transaction.Transaction

/**
 * High-level entry point for managing data persistence and connection lifecycles.
 *
 * Implementations of this class are responsible for configuring the underlying database
 * driver, establishing connections, and providing the transactional context required
 * for repository operations.
 */
abstract class Persistence : AutoCloseable {
    /**
     * Internal factory method to create a new [Transaction] instance.
     * 
     * @return A newly initialized transaction.
     */
    abstract suspend fun createTransaction(): Transaction

    /**
     * Orchestrates a unit of work within a managed transactional scope.
     *
     * This method ensures that a transaction is correctly started, committed upon 
     * successful completion of the [block], or rolled back if an exception occurs.
     *
     * @param T The return type of the transactional block.
     * @param block A suspending functional block to execute within the transaction context.
     * @return The result produced by the [block].
     * @throws Exception If the [block] fails or the transaction cannot be finalized.
     */
    suspend fun <T> transaction(block: suspend Transaction.() -> T): T {
        val transaction = createTransaction()

        return try {
            transaction.block().also { 
                transaction.commit()
            }
        } catch (e: Exception) {
            transaction.rollback()
            throw e
        }
    }
}
