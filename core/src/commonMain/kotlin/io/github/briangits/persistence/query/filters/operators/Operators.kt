package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

sealed interface Operator

sealed interface FieldOperator<T : Any, V> : Operator {
    val prop: KProperty1<T, V?>
}

sealed interface ValueOperator<T : Any, V> : FieldOperator<T, V> {
    val value: V
}
