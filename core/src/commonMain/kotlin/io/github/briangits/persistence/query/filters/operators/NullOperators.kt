package io.github.briangits.persistence.query.filters.operators

import io.github.briangits.persistence.query.properties.property.AnyProperty

sealed interface NullOperator : FieldOperator<Any?>

data class IsNull(
    override val path: AnyProperty<Any?>
) : NullOperator

data class IsNotNull(
    override val path: AnyProperty<Any?>
) : NullOperator
