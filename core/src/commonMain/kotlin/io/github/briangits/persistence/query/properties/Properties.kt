package io.github.briangits.persistence.query.properties

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

interface Properties<T> {

    operator fun <V> KProperty1<T, V>.getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): PropertyPath<V> =
        DirectProperty(this)

}