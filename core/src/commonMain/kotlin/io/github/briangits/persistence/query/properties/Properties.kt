package io.github.briangits.persistence.query.properties

import io.github.briangits.persistence.query.properties.property.Property
import io.github.briangits.persistence.query.properties.property.directPath
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

abstract class Properties<T : Any> : BaseProperties<T> {

    override operator fun <V> KProperty1<T, V>.getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): Property<T, V> = directPath()

}