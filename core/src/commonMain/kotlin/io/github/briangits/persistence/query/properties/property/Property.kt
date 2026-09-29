package io.github.briangits.persistence.query.properties.property

import kotlin.reflect.KProperty1

data class Property<in R : Any, T>(
    val property: KProperty1<in R, T>
) : BaseProperty<R, T> {

    override fun get(receiver: R): T =
        property.get(receiver)

}

fun <R : Any, T> KProperty1<R, T>.directPath(): Property<R, T> =
    Property(this)
