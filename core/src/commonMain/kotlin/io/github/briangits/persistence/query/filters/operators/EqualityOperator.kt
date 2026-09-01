package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

/**
 * Represents equality-based filter operations.
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
sealed interface EqualityOperator<T : Any, V> : ValueOperator<T, V>

/**
 * Filters for entities where the specified property [prop] is exactly equal to [value].
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
data class Eq<T : Any, V>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : EqualityOperator<T, V>

/**
 * Filters for entities where the specified property [prop] is not equal to [value].
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
data class NEq<T : Any, V>(
    override val prop: KProperty1<T, V?>,
    override val value: V
) : EqualityOperator<T, V>
