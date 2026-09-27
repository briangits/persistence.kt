package io.github.briangits.persistence.query

import io.github.briangits.persistence.query.filters.FiltersImpl
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
import io.github.briangits.persistence.query.filters.operators.Lt
import io.github.briangits.persistence.query.filters.operators.Lte
import io.github.briangits.persistence.query.filters.operators.Matches
import io.github.briangits.persistence.query.filters.operators.NEq
import io.github.briangits.persistence.query.filters.operators.Not
import io.github.briangits.persistence.query.filters.operators.NotIn
import io.github.briangits.persistence.query.filters.operators.OneOf
import io.github.briangits.persistence.query.filters.operators.Operator
import io.github.briangits.persistence.query.filters.operators.StartsWith
import io.github.briangits.persistence.query.properties.Properties
import io.github.briangits.persistence.query.properties.PropertyPath

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
private typealias Exact = kotlin.internal.Exact

interface IFilters<T : Any, TSelf>
    where TSelf : IFilters<T, TSelf>, TSelf : Properties<T> {

    val factory: () -> TSelf

    val operators: List<Operator>

    fun add(operator: Operator)

    // Equality
    infix fun <V> PropertyPath<@Exact V>.eq(value: V) =
        if (value == null) isNull()
        else add(operator = Eq(path = this, value))

    infix fun <V> PropertyPath<@Exact V>.neq(value: V) =
        if (value == null) isNotNull()
        else add(operator = NEq(path = this, value))

    // Comparison
    infix fun <V : Comparable<V>> PropertyPath<V?>.gt(value: V) =
        add(operator = Gt(path = this, value))

    infix fun <V : Comparable<V>> PropertyPath<V?>.gte(value: V) =
        add(operator = Gte(path = this, value))

    infix fun <V : Comparable<V>> PropertyPath<V?>.lt(value: V) =
        add(operator = Lt(path = this, value))

    infix fun <V : Comparable<V>> PropertyPath<V?>.lte(value: V) =
        add(operator = Lte(path = this, value))

    fun <V : Comparable<V>> PropertyPath<V?>.between(start: V, end: V) =
        add(operator = Between(path = this, start, end))

    // Strings
    infix fun PropertyPath<String?>.contains(value: String) =
        add(operator = Contains(path = this, value))

    infix fun PropertyPath<String?>.startsWith(value: String) =
        add(operator = StartsWith(path = this, value))

    infix fun PropertyPath<String?>.endsWith(value: String) =
        add(operator = EndsWith(path = this, value))

    infix fun PropertyPath<String?>.matches(value: String) =
        add(operator = Matches(path = this, value))

    infix fun PropertyPath<String?>.matches(value: Regex) =
        add(operator = Matches(path = this, value.pattern))

    // Arrays
    infix fun <V> PropertyPath<V?>.`in`(values: Iterable<V>) =
        add(operator = In(path = this, values))

    infix fun <V> PropertyPath<V?>.notIn(value: Iterable<V>) =
        add(operator = NotIn(path = this, value))

    // Null
    fun <V> PropertyPath<V?>.isNull() =
        add(operator = IsNull(path = this))

    fun <V> PropertyPath<V?>.isNotNull() =
        add(operator = IsNotNull(path = this))

    // Logical
    fun allOf(block: TSelf.() -> Unit) =
        add(operator = AllOf(operators = factory().apply(block).operators))

    fun oneOf(block: TSelf.() -> Unit) =
        add(operator = OneOf(operators = factory().apply(block).operators))

    fun not(block: TSelf.() -> Unit) =
        add(operator = Not(operators = factory().apply(block).operators))

    fun build(): AllOf

}

interface Filters<T : Any, TSelf> : IFilters<T, TSelf>, Properties<T>
        where TSelf : Filters<T, TSelf>

fun <T : Any, TSelf : Filters<T, TSelf>> Filters(
    factory: () -> TSelf
): Filters<T, TSelf> = FiltersImpl(factory)
