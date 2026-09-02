package io.github.briangits.persistence.uow

/**
 * A functional hook executed before the transaction is committed.
 *
 * @param T The Unit of Work type.
 */
typealias BeforeCommitHook<T> = suspend T.() -> Unit

/**
 * A functional hook executed after the transaction has been successfully committed.
 */
typealias AfterCommitHook = suspend () -> Unit
