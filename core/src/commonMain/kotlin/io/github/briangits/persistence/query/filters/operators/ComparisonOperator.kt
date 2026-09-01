package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

/**
 * Represents comparison-based filter operations for comparable types.
 *
 * @param T The entity class containing the property.
 * @param V The comparable property type.
 */
sealed interface ComparisonOperator<T : Any, V : Comparable<V>> : FieldOperator<T, V>

/**
 * Represents a comparison operation targeting a single [value] expression.
 *
 * @param T The entity class containing the property.
 * @param V The comparable property type.
 */
sealed interface ValueComparisonOperator<T : Any, V : Comparable<V>> :
    ComparisonOperator<T, V>,
    ValueOperator<T, V>

/**
 * Filters for entities where the specified property [prop] is strictly greater than [value].
 *
 * @param T The entity class containing the property.
 * @param V The comparable property type.
 */
data class Gt<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : ValueComparisonOperator<T, V>

/**
 * Filters for entities where the specified property [prop] is greater than or equal to [value].
 *
 * @param T The entity class containing the property.
 * @param V The comparable property type.
 */
data class Gte<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : ValueComparisonOperator<T, V>

/**
 * Filters for entities where the specified property [prop] is strictly less than [value].
 *
 * @param T The entity class containing the property.
 * @param V The comparable property type.
 */
data class Lt<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : ValueComparisonOperator<T, V>

/**
 * Filters for entities where the specified property [prop] is less than or equal to [value].
 *
 * @param T The entity class containing the property.
 * @param V The comparable property type.
 */
data class Lte<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : ValueComparisonOperator<T, V>

/**
 * Filters for entities where the specified property [prop] falls
 * inclusively within the range defined by [start] and [end].
 *
 * @param T The entity class containing the property.
 * @param V The comparable property type.
 * @param start The inclusive lower bound of the range.
 * @param end The inclusive upper bound of the range.
 */
data class Between<T : Any, V : Comparable<V>>(
    override val prop: KProperty1<T, V?>,
    val start: V,
    val end: V
) : ComparisonOperator<T, V>
