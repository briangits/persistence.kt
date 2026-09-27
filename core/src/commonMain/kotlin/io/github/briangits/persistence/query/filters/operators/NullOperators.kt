package io.github.briangits.persistence.query.filters.operators

import io.github.briangits.persistence.query.properties.PropertyPath

sealed interface NullOperator : FieldOperator<Any?>

data class IsNull(
    override val path: PropertyPath<Any?>
) : NullOperator

data class IsNotNull(
    override val path: PropertyPath<Any?>
) : NullOperator
