package io.github.briangits.persistence.query.filters.operators

import io.github.briangits.persistence.query.properties.property.AnyProperty

sealed interface Operator

sealed interface FieldOperator<out T> : Operator {

    val path: AnyProperty<T?>

}

sealed interface ValueOperator<out T> : FieldOperator<T> {

    val value: T

}
