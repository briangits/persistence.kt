package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

/**
 * Root interface representing a database query filter operator.
 * 
 * Custom operators should implement this interface or one of its specialized sub-interfaces
 * to integrate with the type-safe persistence filter system.
 */
sealed interface Operator

/**
 * An [Operator] that targets a specific member property [prop] of an entity of type [T].
 *
 * @param T The entity class containing the property.
 * @param V The type of the property being filtered.
 */
sealed interface FieldOperator<T : Any, V> : Operator {
    /**
     * The Kotlin property reference representing the entity field.
     */
    val prop: KProperty1<T, V?>
}

/**
 * A [FieldOperator] that performs a comparison against a concrete, singular [value].
 *
 * @param T The entity class containing the property.
 * @param V The type of the value being compared.
 */
sealed interface ValueOperator<T : Any, V> : FieldOperator<T, V> {
    /**
     * The literal value used in the comparison operation.
     */
    val value: V
}
