package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

/**
 * Represents specialized text/string-matching filter operations.
 *
 * @param T The entity class containing the property.
 */
sealed interface StringOperator<T : Any> : ValueOperator<T, String>

/**
 * Filters for entities where the specified property [prop]
 * matches an SQL-pattern [value].
 *
 * @param T The entity class containing the property.
 */
data class Like<T : Any>(
    override val prop: KProperty1<T, String?>,
    override val value: String
) : StringOperator<T>

/**
 * Filters for entities where the specified property [prop]
 * contains the specified [value] substring.
 *
 * @param T The entity class containing the property.
 */
data class Contains<T : Any>(
    override val prop: KProperty1<T, String?>,
    override val value: String
) : StringOperator<T>

/**
 * Filters for entities where the specified property [prop]
 * begins with the specified [value] prefix.
 *
 * @param T The entity class containing the property.
 */
data class StartsWith<T : Any>(
    override val prop: KProperty1<T, String?>,
    override val value: String
) : StringOperator<T>

/**
 * Filters for entities where the specified property [prop]
 * ends with the specified [value] suffix.
 *
 * @param T The entity class containing the property.
 */
data class EndsWith<T : Any>(
    override val prop: KProperty1<T, String?>,
    override val value: String
) : StringOperator<T>

/**
 * Filters for entities where the specified property [prop]
 * matches the regex pattern specified in [value].
 *
 * @param T The entity class containing the property.
 */
data class Matches<T : Any>(
    override val prop: KProperty1<T, String?>,
    override val value: String
) : StringOperator<T>
