package io.github.briangits.persistence.query.filters.operators

import io.github.briangits.persistence.query.properties.PropertyPath

sealed interface ArrayOperator<out T> : FieldOperator<T> {

    val values: Iterable<T>

}

data class In<out T>(
    override val path: PropertyPath<T?>,
    override val values: Iterable<T>
) : ArrayOperator<T>

data class NotIn<out T>(
    override val path: PropertyPath<T?>,
    override val values: Iterable<T>
) : ArrayOperator<T>
