package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

/**
 * Represents null-safety constraints on entity properties.
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
sealed interface NullOperator<T : Any, V> : FieldOperator<T, V>

/**
 * Filters for entities where the specified property [prop] has no value (is null).
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
data class IsNull<T : Any, V>(
    override val prop: KProperty1<T, V?>
) : NullOperator<T, V>

/**
 * Filters for entities where the specified property [prop] has a valid, non-null value.
 *
 * @param T The entity class containing the property.
 * @param V The property type.
 */
data class IsNotNull<T : Any, V>(
    override val prop: KProperty1<T, V?>
) : NullOperator<T, V>
