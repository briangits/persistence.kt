package io.github.briangits.persistence.query.properties.property

sealed interface BaseProperty<in R : Any, out T> {

    fun get(receiver: R): T

    operator fun invoke(receiver: R): T = get(receiver)

}

typealias AnyProperty<T> = BaseProperty<*, T>
