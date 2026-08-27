package io.github.briangits.persistence.exposed.relations

import org.jetbrains.exposed.v1.core.Column
import kotlin.reflect.KProperty1

interface RelationsBuilder<T> {
    infix fun <V> KProperty1<T, V>.mapsTo(column: Column<V>)
}

internal class RelationsBuilderImpl<T : Any> : RelationsBuilder<T> {
    private val relations = mutableSetOf<PropertyColumRelation<T, *>>()

    override infix fun <V> KProperty1<T, V>.mapsTo(column: Column<V>) {
        relations.add(PropertyColumRelation(this, column))
    }

    fun build(): PropertyColumRelations<T> = relations
}
