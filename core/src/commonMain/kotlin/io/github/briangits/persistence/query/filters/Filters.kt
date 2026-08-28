package io.github.briangits.persistence.query

import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.filters.operators.Between
import io.github.briangits.persistence.query.filters.operators.Contains
import io.github.briangits.persistence.query.filters.operators.EndsWith
import io.github.briangits.persistence.query.filters.operators.Eq
import io.github.briangits.persistence.query.filters.operators.Gt
import io.github.briangits.persistence.query.filters.operators.Gte
import io.github.briangits.persistence.query.filters.operators.In
import io.github.briangits.persistence.query.filters.operators.IsNotNull
import io.github.briangits.persistence.query.filters.operators.IsNull
import io.github.briangits.persistence.query.filters.operators.Like
import io.github.briangits.persistence.query.filters.operators.Lt
import io.github.briangits.persistence.query.filters.operators.Lte
import io.github.briangits.persistence.query.filters.operators.Matches
import io.github.briangits.persistence.query.filters.operators.NEq
import io.github.briangits.persistence.query.filters.operators.Not
import io.github.briangits.persistence.query.filters.operators.NotIn
import io.github.briangits.persistence.query.filters.operators.OneOf
import io.github.briangits.persistence.query.filters.operators.Operator
import io.github.briangits.persistence.query.filters.operators.StartsWith
import kotlin.reflect.KProperty1

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
private typealias Exact = kotlin.internal.Exact

open class Filters<T : Any, TInstance: Filters<T, TInstance>>(
    private val factory: () -> TInstance
) {
    private val operators = mutableListOf<Operator>()

    fun add(operator: Operator) {
        operators.add(operator)
    }

    infix fun <V> KProperty1<T, @Exact V>.eq(value: V) =
        if (value == null) isNull() else add(Eq(this, value))
    infix fun <V> KProperty1<T, @Exact V>.neq(value: V) =
        if (value == null) isNotNull() else add(NEq(this, value))

    infix fun <V : Comparable<V>> KProperty1<T, V?>.gt(value: V) = add(Gt(this, value))
    infix fun <V : Comparable<V>> KProperty1<T, V?>.gte(value: V) = add(Gte(this, value))
    infix fun <V : Comparable<V>> KProperty1<T, V?>.lt(value: V) = add(Lt(this, value))
    infix fun <V : Comparable<V>> KProperty1<T, V?>.lte(value: V) = add(Lte(this, value))
    fun <V : Comparable<V>> KProperty1<T, V?>.between(start: V, end: V) =
        add(Between(this, start, end))

    infix fun KProperty1<T, String?>.like(value: String) = add(Like(this, value))
    infix fun KProperty1<T, String?>.contains(value: String) = add(Contains(this, value))
    infix fun KProperty1<T, String?>.startsWith(value: String) = add(StartsWith(this, value))
    infix fun KProperty1<T, String?>.endsWith(value: String) = add(EndsWith(this, value))
    infix fun KProperty1<T, String?>.matches(value: String) = add(Matches(this, value))
    infix fun KProperty1<T, String?>.matches(value: Regex) = add(Matches(this, value.pattern))


    infix fun <V> KProperty1<T, V?>.`in`(values: Iterable<V>) = add(In(this, values))
    infix fun <V> KProperty1<T, V?>.notIn(value: Iterable<V>) = add(NotIn(this, value))

    fun <V> KProperty1<T, V?>.isNull() = add(IsNull(this))
    fun <V> KProperty1<T, V?>.isNotNull() = add(IsNotNull(this))

    fun allOf(builder: TInstance.() -> Unit) =
        add(AllOf<T>(factory().apply(builder).operators))

    fun oneOf(builder: TInstance.() -> Unit) =
        add(OneOf<T>(factory().apply(builder).operators))

    fun not(builder: TInstance.() -> Unit) =
        add(Not<T>(factory().apply(builder).operators))

    fun build(): AllOf<T> = AllOf(operators)
}
