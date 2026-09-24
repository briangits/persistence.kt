package io.github.briangits.persistence.exposed

import io.github.briangits.persistence.Persistence
import io.github.briangits.persistence.exposed.repository.RepositoryRegistry
import io.github.briangits.persistence.exposed.transaction.ExposedTransaction
import io.github.briangits.persistence.exposed.transaction.TransactionControl
import io.github.briangits.persistence.transaction.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager

/**
 * Implementation of [Persistence] using the JetBrains Exposed library.
 *
 * Manages database connection lifecycle, transactional scopes, and repository resolution
 * within the Exposed framework.
 */
internal class ExposedPersistence(
    private val database: Database,
    private val registry: RepositoryRegistry,
    private val dispatcher: CoroutineDispatcher
) : Persistence() {
    val initialization = Mutex()
    private var initialized: Boolean = false

    @Volatile private var isClosed: Boolean = false

    override suspend fun initialize() =
        withContext(dispatcher) {
            initialization.withLock {
                if (initialized) return@withContext

                suspendTransaction (database) { exec("SELECT 1") }
                initialized = true
            }
        }

    override fun close() {
        if (isClosed) return
        isClosed = true

        TransactionManager.closeAndUnregister(database)
    }

    override suspend fun createTransaction(): Transaction {
        require(initialized) { "Persistence is not initialized" }

        val transaction = database.transactionManager.newTransaction()
        val control = TransactionControl(transaction, dispatcher)

        return ExposedTransaction(control, registry)
    }
}

/**
 * Creates and configures a persistence instance
 * backed by [io.github.briangits.persistence.exposed.ExposedPersistence]
 *
 * @param database The Exposed [Database] instance.
 * @param dispatcher The [CoroutineDispatcher] to use for database operations. Defaults to [Dispatchers.IO].
 * @param block A configuration block for defining repository bindings using the [PersistenceBuilder] DSL.
 * @return A configured [ExposedPersistence] instance.
 */
@Suppress("FunctionName")
fun ExposedPersistence(
    database: Database,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: PersistenceBuilder.() -> Unit
): Persistence = PersistenceBuilderImpl(database, dispatcher, block).build()
