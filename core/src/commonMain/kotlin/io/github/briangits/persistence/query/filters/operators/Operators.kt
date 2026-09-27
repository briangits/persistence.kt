package io.github.briangits.persistence.query.filters.operators

import io.github.briangits.persistence.properties.PropertyPath

sealed interface Operator

sealed interface FieldOperator<out T> : Operator {

    val path: PropertyPath<T?>

}

sealed interface ValueOperator<out T> : FieldOperator<T> {

    val value: T

}
