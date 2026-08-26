package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

sealed interface NullOperator<T : Any, V> : FieldOperator<T, V>

data class IsNull<T : Any, V>(
    override val prop: KProperty1<T, V?>
) : NullOperator<T, V>

data class IsNotNull<T : Any, V>(
    override val prop: KProperty1<T, V?>
) : NullOperator<T, V>
