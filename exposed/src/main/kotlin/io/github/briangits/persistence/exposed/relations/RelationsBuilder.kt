package io.github.briangits.persistence.exposed.relations

import org.jetbrains.exposed.v1.core.Column
import kotlin.reflect.KProperty1

/**
 * DSL builder interface for defining mapping relationships between entity properties
 * and database columns.
 *
 * @param T The entity type.
 */
interface RelationsBuilder<T> {
    /**
     * Defines a mapping relationship between an entity property and a database column.
     *
     * @param T The entity type.
     * @param V The property/column value type.
     * @param column The database column.
     */
    infix fun <V> KProperty1<T, V>.mapsTo(column: Column<V>)
}

/**
 * Internal implementation of [RelationsBuilder].
 *
 * @param T The entity type.
 */
internal class RelationsBuilderImpl<T : Any> : RelationsBuilder<T> {
    private val relations = mutableSetOf<PropertyColumRelation<T, *>>()

    override infix fun <V> KProperty1<T, V>.mapsTo(column: Column<V>) {
        relations.add(PropertyColumRelation(this, column))
    }

    /**
     * Builds and returns the set of configured [PropertyColumRelation]s.
     */
    fun build(): PropertyColumRelations<T> = relations
}
