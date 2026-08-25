package io.github.briangits.persistence.exposed

import io.github.briangits.persistence.Persistence
import io.github.briangits.persistence.exposed.repository.RepositoryRegistry
import io.github.briangits.persistence.exposed.transaction.ExposedTransaction
import io.github.briangits.persistence.exposed.transaction.TransactionControl
import io.github.briangits.persistence.transaction.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager

class ExposedPersistence internal constructor(
    private val database: Database,
    private val registry: RepositoryRegistry,
    private val dispatcher: CoroutineDispatcher
) : Persistence() {
    val initialization = Mutex()
    private var initialized: Boolean = false

    override suspend fun initialize() =
        withContext(dispatcher) {
            initialization.withLock {
                if (initialized) return@withContext

                suspendTransaction (database) { exec("SELECT 1") }
                initialized = true
            }
        }

    override suspend fun close() =
        withContext(dispatcher) {
            initialization.withLock {
                if (!initialized) return@withContext

                TransactionManager.closeAndUnregister(database)
                initialized = false
            }
        }

    override suspend fun createTransaction(): Transaction {
        val transaction = database.transactionManager.newTransaction()
        val control = TransactionControl(transaction, dispatcher)

        return ExposedTransaction(control, registry)
    }
}

fun ExposedPersistence(
    database: Database,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: PersistenceBuilder.() -> Unit
): ExposedPersistence = PersistenceBuilderImpl(database, dispatcher, block).build()
