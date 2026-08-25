package io.github.briangits.persistence

import io.github.briangits.persistence.transaction.Transaction

abstract class Persistence {
    abstract suspend fun initialize()

    abstract suspend fun close()

    abstract suspend fun createTransaction(): Transaction

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
