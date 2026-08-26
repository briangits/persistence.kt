package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

sealed interface ComparisonOperator<T : Any, V : Comparable<V>> : FieldOperator<T, V>

sealed interface ValueComparisonOperator<T : Any, V : Comparable<V>> :
    ComparisonOperator<T, V>,
    ValueOperator<T, V>

data class Gt<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : ValueComparisonOperator<T, V>

data class Gte<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : ValueComparisonOperator<T, V>

data class Lt<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : ValueComparisonOperator<T, V>

data class Lte<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : ValueComparisonOperator<T, V>

data class Between<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    val start: V,
    val end: V
) : ComparisonOperator<T, V>
