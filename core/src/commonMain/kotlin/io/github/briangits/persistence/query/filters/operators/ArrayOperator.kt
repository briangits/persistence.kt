package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

/**
 * Represents containment-based filter operations against a collection of values.
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
sealed interface ArrayOperator<T : Any, V> : FieldOperator<T, V> {
    /**
     * The collection of candidate values for comparison.
     */
    val values: Iterable<V>
}

/**
 * Filters for entities where the specified property [prop] has a value
 * that matches any value in [values].
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
data class In<T : Any, V>(
    override val prop: KProperty1<T, V?>,
    override val values: Iterable<V>
) : ArrayOperator<T, V>

/**
 * Filters for entities where the specified property [prop] has a value that
 * matches no values in [values].
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
data class NotIn<T : Any, V>(
    override val prop: KProperty1<T, V?>,
    override val values: Iterable<V>
) : ArrayOperator<T, V>
