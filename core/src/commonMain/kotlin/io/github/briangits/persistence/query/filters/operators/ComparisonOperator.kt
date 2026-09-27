package io.github.briangits.persistence.query.filters.operators

import io.github.briangits.persistence.properties.PropertyPath

sealed interface ComparisonOperator<T : Comparable<T>> : FieldOperator<T>

sealed interface ValueComparisonOperator<T : Comparable<T>> :
    ComparisonOperator<T>,
    ValueOperator<T>

data class Gt<T : Comparable<T>>(
    override val path: PropertyPath<T?>,
    override val value: T
) : ValueComparisonOperator<T>

data class Gte<T : Comparable<T>>(
    override val path: PropertyPath<T?>,
    override val value: T
) : ValueComparisonOperator<T>

data class Lt<T : Comparable<T>>(
    override val path: PropertyPath<T?>,
    override val value: T
) : ValueComparisonOperator<T>

data class Lte<T : Comparable<T>>(
    override val path: PropertyPath<T?>,
    override val value: T
) : ValueComparisonOperator<T>

data class Between<T : Comparable<T>>(
    override val path: PropertyPath<T?>,
    val start: T,
    val end: T
) : ComparisonOperator<T>
