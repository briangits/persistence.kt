package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

sealed interface EqualityOperator<T : Any, V> : ValueOperator<T, V>

data class Eq<T : Any, V>(
    override val prop: KProperty1<T, V>,
    override val value: V
) : EqualityOperator<T, V>

data class NEq<T : Any, V>(
    override val prop: KProperty1<T, V>,
    override val value: V
) : EqualityOperator<T, V>
