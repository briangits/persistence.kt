package io.github.briangits.persistence.query.properties

import io.github.briangits.persistence.query.filters.Filters
import io.github.briangits.persistence.query.filters.FiltersImpl
import io.github.briangits.persistence.query.filters.build
import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.properties.property.Property
import io.github.briangits.persistence.query.properties.property.directPath
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

open class Properties<T : Any, TSelf : Properties<T, TSelf>> :
    BaseProperties<T>,
    Filters<TSelf> by FiltersImpl() {

    override operator fun <V> KProperty1<T, V>.getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): Property<T, V> = directPath()

}

fun <T : Any, TSelf : Properties<T, TSelf>> TSelf.buildFilters(factory: () -> TSelf): AllOf =
    build(factory)
