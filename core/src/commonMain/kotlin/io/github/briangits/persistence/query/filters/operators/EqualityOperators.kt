package io.github.briangits.persistence.query.filters.operators

import io.github.briangits.persistence.query.properties.PropertyPath

sealed interface EqualityOperator<out T> : ValueOperator<T>

data class Eq<out T>(
    override val path: PropertyPath<T?>,
    override val value: T
) : EqualityOperator<T>

data class NEq<out T>(
    override val path: PropertyPath<T?>,
    override val value: T
) : EqualityOperator<T>
