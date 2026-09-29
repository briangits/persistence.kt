package io.github.briangits.persistence.query

import io.github.briangits.persistence.query.filters.operators.Between
import io.github.briangits.persistence.query.filters.operators.Contains
import io.github.briangits.persistence.query.filters.operators.EndsWith
import io.github.briangits.persistence.query.filters.operators.Eq
import io.github.briangits.persistence.query.filters.operators.Gt
import io.github.briangits.persistence.query.filters.operators.Gte
import io.github.briangits.persistence.query.filters.operators.In
import io.github.briangits.persistence.query.filters.operators.IsNotNull
import io.github.briangits.persistence.query.filters.operators.IsNull
import io.github.briangits.persistence.query.filters.operators.Lt
import io.github.briangits.persistence.query.filters.operators.Lte
import io.github.briangits.persistence.query.filters.operators.Matches
import io.github.briangits.persistence.query.filters.operators.NEq
import io.github.briangits.persistence.query.filters.operators.NotIn
import io.github.briangits.persistence.query.filters.operators.Operator
import io.github.briangits.persistence.query.filters.operators.StartsWith
import io.github.briangits.persistence.query.properties.property.AnyProperty

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
private typealias Exact = kotlin.internal.Exact

interface BaseFilters<T : Any, TSelf : BaseFilters<T, TSelf>> {

    fun add(operator: Operator)

    // Equality
    infix fun <V> AnyProperty<@Exact V>.eq(value: V) =
        if (value == null) isNull()
        else add(operator = Eq(path = this, value))

    infix fun <V> AnyProperty<@Exact V>.neq(value: V) =
        if (value == null) isNotNull()
        else add(operator = NEq(path = this, value))

    // Comparison
    infix fun <V : Comparable<V>> AnyProperty<V?>.gt(value: V) =
        add(operator = Gt(path = this, value))

    infix fun <V : Comparable<V>> AnyProperty<V?>.gte(value: V) =
        add(operator = Gte(path = this, value))

    infix fun <V : Comparable<V>> AnyProperty<V?>.lt(value: V) =
        add(operator = Lt(path = this, value))

    infix fun <V : Comparable<V>> AnyProperty<V?>.lte(value: V) =
        add(operator = Lte(path = this, value))

    fun <V : Comparable<V>> AnyProperty<V?>.between(start: V, end: V) =
        add(operator = Between(path = this, start, end))

    // Strings
    infix fun AnyProperty<String?>.contains(value: String) =
        add(operator = Contains(path = this, value))

    infix fun AnyProperty<String?>.startsWith(value: String) =
        add(operator = StartsWith(path = this, value))

    infix fun AnyProperty<String?>.endsWith(value: String) =
        add(operator = EndsWith(path = this, value))

    infix fun AnyProperty<String?>.matches(value: String) =
        add(operator = Matches(path = this, value))

    infix fun AnyProperty<String?>.matches(value: Regex) =
        add(operator = Matches(path = this, value.pattern))

    // Arrays
    infix fun <V> AnyProperty<V?>.`in`(values: Iterable<V>) =
        add(operator = In(path = this, values))

    infix fun <V> AnyProperty<V?>.notIn(value: Iterable<V>) =
        add(operator = NotIn(path = this, value))

    // Null
    fun <V> AnyProperty<V?>.isNull() =
        add(operator = IsNull(path = this))

    fun <V> AnyProperty<V?>.isNotNull() =
        add(operator = IsNotNull(path = this))

    // Logical
    fun allOf(block: TSelf.() -> Unit)

    fun oneOf(block: TSelf.() -> Unit)

    fun not(block: TSelf.() -> Unit)

    fun compile(factory: () -> TSelf): List<Operator>

}
