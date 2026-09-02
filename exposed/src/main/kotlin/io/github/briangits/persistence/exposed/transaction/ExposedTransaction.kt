package io.github.briangits.persistence.exposed.transaction

import io.github.briangits.persistence.exposed.repository.ExposedRepositoryFactory
import io.github.briangits.persistence.exposed.repository.RepositoryRegistry
import io.github.briangits.persistence.repository.RepositoryFactory
import io.github.briangits.persistence.transaction.Transaction

/**
 * Internal implementation of the [Transaction] interface, providing atomic transactional
 * behavior over Exposed's JDBC transactions.
 *
 * Delegates repository factory operations to [ExposedRepositoryFactory] and manages
 * transaction lifecycle via [TransactionControl].
 */
internal class ExposedTransaction(
    private val control: TransactionControl,
    registry: RepositoryRegistry
) : Transaction,
    RepositoryFactory by ExposedRepositoryFactory(control, registry) {
    override suspend fun commit(): Unit = control.commit()
    override suspend fun rollback(): Unit = control.rollback()
}
