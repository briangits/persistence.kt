package io.github.briangits.persistence.uow

import io.github.briangits.persistence.repository.RepositoryFactory
import io.github.briangits.persistence.transaction.Transaction

/**
 * Base class for implementing the Unit of Work pattern, managing a transactional context
 * and providing lifecycle hooks for pre- and post-commit operations.
 *
 * @param T The specific implementation type of the Unit of Work.
 * @param transaction The underlying transaction providing repository access
 * and commit/rollback capabilities.
 */
abstract class UnitOfWork<T : UnitOfWork<T>>(
    private val transaction: Transaction
) : RepositoryFactory by transaction {
    private val beforeCommitHooks = mutableListOf<BeforeCommitHook<T>>()
    private val afterCommitHooks = mutableListOf<AfterCommitHook>()

    /**
     * Registers a hook to be executed immediately before the transaction is committed.
     *
     * @param hook The hook function to register.
     */
    fun beforeCommit(hook: BeforeCommitHook<T>) { beforeCommitHooks.add(hook) }

    /**
     * Registers a hook to be executed immediately after the transaction is successfully committed.
     *
     * @param hook The hook function to register.
     */
    fun afterCommit(hook: AfterCommitHook) { afterCommitHooks.add(hook) }

    /**
     * Commits the transaction after executing all registered `beforeCommit` hooks.
     * Executes `afterCommit` hooks if the commit is successful.
     *
     * If an error occurs during hook execution or commit, the transaction is rolled back.
     */
    suspend fun commit() {
        try {
            @Suppress("UNCHECKED_CAST")
            for (hook in beforeCommitHooks) hook(this as T)

            transaction.commit()
        } catch (e: Throwable) {
            runCatching { rollback() }
                .onFailure { e.addSuppressed(it) }

            throw e
        }

        for (hook in afterCommitHooks) hook()
    }

    /**
     * Rolls back the current transaction.
     */
    suspend fun rollback() = transaction.rollback()

    /**
     * Executes a given block of code within the transactional context of this Unit of Work.
     * Rolls back the transaction if the block fails.
     *
     * @param R The return type of the block.
     * @param block The block to execute.
     * @return The result of the block.
     */
    suspend fun <R> execute(block: suspend T.() -> R): R {
        return try {
            @Suppress("UNCHECKED_CAST")
            block( this as T)
        } catch (e: Throwable) {
            runCatching { rollback() }
                .onFailure { e.addSuppressed(it) }

            throw e
        }
    }

    /**
     * Executes a given block of code and commits the transaction upon successful completion.
     * Rolls back the transaction if the block fails.
     *
     * @param R The return type of the block.
     * @param block The block to execute.
     * @return The result of the block.
     */
    suspend fun <R> run(block: suspend T.() -> R): R =
        execute(block).also { commit() }

}
