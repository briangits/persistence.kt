package io.github.briangits.persistence.query.properties

import io.github.briangits.persistence.query.properties.property.BaseProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

interface BaseProperties<T : Any> {

    operator fun <V> KProperty1<T, V>.getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): BaseProperty<T, V>

}
