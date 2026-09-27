package io.github.briangits.persistence.properties

import kotlin.reflect.KProperty1

data class NestedProperty<out T, out V>(
    val parent: PropertyPath<T>,
    val property: KProperty1<out T, V>
) : PropertyPath<V>()

fun <T, V> KProperty1<T, V>.nestedPath(parent: PropertyPath<T>): NestedProperty<T, V> =
    NestedProperty(parent, this)

fun <T, V> PropertyPath<T>.nested(property: KProperty1<T, V>): NestedProperty<T, V> =
    property.nestedPath(this)
