package io.github.briangits.persistence.properties

import kotlin.reflect.KProperty1

data class DirectProperty<T, V>(
    val property: KProperty1<T, V>
) : PropertyPath<V>()

fun <T, V> KProperty1<T, V>.directPath(): DirectProperty<T, V> = DirectProperty(this)
