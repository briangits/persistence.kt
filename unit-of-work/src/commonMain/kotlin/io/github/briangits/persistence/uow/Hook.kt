package io.github.briangits.persistence.uow

typealias BeforeCommitHook<T> = suspend T.() -> Unit

typealias AfterCommitHook = suspend () -> Unit
