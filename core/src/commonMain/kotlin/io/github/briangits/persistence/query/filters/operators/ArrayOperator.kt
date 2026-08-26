package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

sealed interface ArrayOperator<T : Any, V> : FieldOperator<T, V> {
    val values: Iterable<V>
}

data class In<T : Any, V>(
    override val prop: KProperty1<T, V?>,
    override val values: Iterable<V>
) : ArrayOperator<T, V>

data class NotIn<T : Any, V>(
    override val prop: KProperty1<T, V?>,
    override val values: Iterable<V>
) : ArrayOperator<T, V>
