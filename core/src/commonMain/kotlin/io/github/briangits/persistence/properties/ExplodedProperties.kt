package io.github.briangits.persistence.properties

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

abstract class ExplodedProperties<out P, T>(private val parent: PropertyPath<T>) {

    constructor(property: KProperty1<P, T>) : this(
        parent = property.directPath()
    )

    constructor(
        parent: ExplodedProperties<*, P>,
        property: KProperty1<P, T>
    ) : this(
        parent = property.nestedPath(parent.parent)
    )

    operator fun getValue(
        thisRef: Any?,
        desc: Any?
    ): PropertyPath<T> = parent

    operator fun <V> KProperty1<T, V>.getValue(
        thisRef: Any?,
        desc: KProperty<*>
    ): NestedProperty<T, V> =
        NestedProperty(parent, property = this)

}

typealias explode<P, T> = ExplodedProperties<P, T>

fun <T : explode<*, *>> explode(properties: T, block: T.() -> Unit) =
    properties.block()
