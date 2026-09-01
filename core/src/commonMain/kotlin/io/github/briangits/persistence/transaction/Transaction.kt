package io.github.briangits.persistence.transaction

import io.github.briangits.persistence.repository.RepositoryFactory

/**
 * Represents a single atomic unit of work within the persistence layer.
 *
 * A transaction ensures that all database operations performed within its scope 
 * follow ACID properties. It provides access to [RepositoryFactory] to allow 
 * retrieval of repositories that participate in the transaction.
 */
interface Transaction : RepositoryFactory {
    /**
     * Persists all pending changes made within this transaction to the database.
     * 
     * Once committed, the changes are permanent and visible to other transactions.
     */
    suspend fun commit()

    /**
     * Discards all pending changes made within this transaction.
     * 
     * Reverts the database state to what it was before the transaction began.
     */
    suspend fun rollback()
}
