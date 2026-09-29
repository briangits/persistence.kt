package io.github.briangits.persistence.query.properties.property

import io.github.briangits.persistence.query.properties.BaseProperties
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

open class ExplodedProperty<in R : Any, T : Any> private constructor(
    private val path: Property<R, T>
) : BaseProperties<T> {

    constructor(property: KProperty1<R, T>) : this(
        path = property.directPath()
    )

    val entries: Set<NestedProperty<T, *>>
    field = mutableSetOf()

    override operator fun <V> KProperty1<T, V>.getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): NestedProperty<T, V> = path.nested(this).also { entries += it }

}

typealias explode<R, T> = ExplodedProperty<R, T>

fun <T : explode<*, *>, R> explode(properties: T, block: T.() -> R): R =
    properties.block()
