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

/**
 * Base class for building type-safe queries using a Kotlin DSL.
 *
 * This class provides a set of infix functions and helper methods to construct
 * complex query filters by referencing entity properties and applying operators.
 *
 * @param T The entity type being queried.
 * @param TInstance The specific implementation type of the filters (usually a subclass of [Filters]).
 * @param factory A factory function to create a new instance of [TInstance] for nested groupings.
 */
open class Filters<T : Any, TInstance: Filters<T, TInstance>>(
    private val factory: () -> TInstance
) {
    private val operators = mutableListOf<Operator>()

    /**
     * Manually adds a custom [Operator] to the filter set.
     *
     * @param operator The operator to add.
     */
    fun add(operator: Operator) {
        operators.add(operator)
    }

    /**
     * Filters for entities where the property is equal to the specified [value].
     * If the [value] is null, this defaults to an [isNull] check.
     */
    infix fun <V> KProperty1<T, @Exact V>.eq(value: V) =
        if (value == null) isNull() else add(Eq(this, value))

    /**
     * Filters for entities where the property is not equal to the specified [value].
     * If the [value] is null, this defaults to an [isNotNull] check.
     */
    infix fun <V> KProperty1<T, @Exact V>.neq(value: V) =
        if (value == null) isNotNull() else add(NEq(this, value))

    /**
     * Filters for entities where the property is strictly greater than [value].
     */
    infix fun <V : Comparable<V>> KProperty1<T, V?>.gt(value: V) = add(Gt(this, value))

    /**
     * Filters for entities where the property is greater than or equal to [value].
     */
    infix fun <V : Comparable<V>> KProperty1<T, V?>.gte(value: V) = add(Gte(this, value))

    /**
     * Filters for entities where the property is strictly less than [value].
     */
    infix fun <V : Comparable<V>> KProperty1<T, V?>.lt(value: V) = add(Lt(this, value))

    /**
     * Filters for entities where the property is less than or equal to [value].
     */
    infix fun <V : Comparable<V>> KProperty1<T, V?>.lte(value: V) = add(Lte(this, value))

    /**
     * Filters for entities where the property value is inclusively between [start] and [end].
     */
    fun <V : Comparable<V>> KProperty1<T, V?>.between(start: V, end: V) =
        add(Between(this, start, end))

    /**
     * Filters for entities where the property matches the SQL-style pattern [value].
     */
    infix fun KProperty1<T, String?>.like(value: String) = add(Like(this, value))

    /**
     * Filters for entities where the property contains the specified [value] substring.
     */
    infix fun KProperty1<T, String?>.contains(value: String) = add(Contains(this, value))

    /**
     * Filters for entities where the property begins with the specified [value] prefix.
     */
    infix fun KProperty1<T, String?>.startsWith(value: String) = add(StartsWith(this, value))

    /**
     * Filters for entities where the property ends with the specified [value] suffix.
     */
    infix fun KProperty1<T, String?>.endsWith(value: String) = add(EndsWith(this, value))

    /**
     * Filters for entities where the property matches the regular expression pattern [value].
     */
    infix fun KProperty1<T, String?>.matches(value: String) = add(Matches(this, value))

    /**
     * Filters for entities where the property matches the provided regular expression.
     */
    infix fun KProperty1<T, String?>.matches(value: Regex) = add(Matches(this, value.pattern))

    /**
     * Filters for entities where the property value is contained within the provided [values] set.
     */
    infix fun <V> KProperty1<T, V?>.`in`(values: Iterable<V>) = add(In(this, values))

    /**
     * Filters for entities where the property value is NOT contained within
     * the provided [value] set.
     */
    infix fun <V> KProperty1<T, V?>.notIn(value: Iterable<V>) = add(NotIn(this, value))

    /**
     * Filters for entities where the property is null.
     */
    fun <V> KProperty1<T, V?>.isNull() = add(IsNull(this))

    /**
     * Filters for entities where the property is not null.
     */
    fun <V> KProperty1<T, V?>.isNotNull() = add(IsNotNull(this))

    /**
     * Groups nested filters with a logical AND.
     */
    fun allOf(builder: TInstance.() -> Unit) =
        add(AllOf<T>(factory().apply(builder).operators))

    /**
     * Groups nested filters with a logical OR.
     */
    fun oneOf(builder: TInstance.() -> Unit) =
        add(OneOf<T>(factory().apply(builder).operators))

    /**
     * Inverts the result of the nested filters.
     */
    fun not(builder: TInstance.() -> Unit) =
        add(Not<T>(factory().apply(builder).operators))

    /**
     * Finalizes the filter construction and returns the root operator.
     */
    fun build(): AllOf<T> = AllOf(operators)
}
