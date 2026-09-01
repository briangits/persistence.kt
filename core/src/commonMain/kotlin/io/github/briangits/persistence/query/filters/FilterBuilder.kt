package io.github.briangits.persistence.query.filters

/**
 * Functional interface representing a block of code used to configure filter criteria.
 *
 * This typealias is typically used as a lambda parameter in repository methods
 * (like `findAll` or `find`) to provide a type-safe context [T] for building query expressions.
 *
 * @param T The type of the filter builder context, usually a subclass of `Filters`.
 */
typealias FilterBuilder<T> = T.() -> Unit
