package io.github.briangits.persistence.exposed.relations

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import kotlin.reflect.KProperty1

interface RelationsBuilder<T> {

    infix fun <V : Any> KProperty1<T, V>.mapsToId(column: Column<EntityID<V>>)

    infix fun <V> KProperty1<T, V>.mapsTo(column: Column<V>)
}

internal class RelationsBuilderImpl<T : Any> : RelationsBuilder<T> {
    private val relations = mutableSetOf<PropertyColumRelation<T, *>>()

    override fun <V : Any> KProperty1<T, V>.mapsToId(column: Column<EntityID<V>>) {
        relations.add(PropertyColumRelation.Id(this, column))
    }

    override infix fun <V> KProperty1<T, V>.mapsTo(column: Column<V>) {
        relations.add(PropertyColumRelation.Field(this, column))
    }

    fun build(): PropertyColumRelations<T> = relations
}
