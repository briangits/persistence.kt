package io.github.briangits.persistence.query.properties.property

import kotlin.reflect.KProperty1

data class NestedProperty<R : Any, T>(
    val parent: Property<*, R>,
    val property: KProperty1<R, T>
) : BaseProperty<R, T> {

    override fun get(receiver: R): T =
        property.get(receiver)

}

fun <R : Any, T> KProperty1<R, T>.nestedPath(parent: Property<*, R>): NestedProperty<R, T> =
    NestedProperty(parent, this)

fun <R : Any, T> Property<*, R>.nested(property: KProperty1<R, T>): NestedProperty<R, T> =
    property.nestedPath(this)
