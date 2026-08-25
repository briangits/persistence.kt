package io.github.briangits.persistence.exposed.transaction

import io.github.briangits.persistence.exposed.repository.ExposedRepositoryFactory
import io.github.briangits.persistence.exposed.repository.RepositoryRegistry
import io.github.briangits.persistence.repository.RepositoryFactory
import io.github.briangits.persistence.transaction.Transaction

internal class ExposedTransaction(
    private val control: TransactionControl,
    registry: RepositoryRegistry
) : Transaction,
    RepositoryFactory by ExposedRepositoryFactory(control, registry) {
    override suspend fun commit(): Unit = control.commit()
    override suspend fun rollback(): Unit = control.rollback()
}
