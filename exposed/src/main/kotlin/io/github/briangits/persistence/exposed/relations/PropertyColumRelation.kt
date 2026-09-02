package io.github.briangits.persistence.exposed.relations

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import kotlin.reflect.KProperty1

/**
 * Represents a binding between a domain entity property and a database column.
 *
 * @param T The entity type.
 * @param V The property/column value type.
 * @property prop The domain entity property reference.
 * @property column The corresponding database column definition.
 */
class PropertyColumRelation<T : Any, V>(
    val prop: KProperty1<T, V>,
    val column: Column<V>
) {
    /**
     * Persists the value from the [entity] property into the [builder] during database updates.
     *
     * @param builder The Exposed [UpdateBuilder] to populate.
     * @param entity The domain entity containing the value.
     */
    fun set(
        builder: UpdateBuilder<Int>,
        entity: T
    ) {
        builder[column] = prop.get(entity)
    }
}

/**
 * A type alias for a set of [PropertyColumRelation]s, representing all mapped properties for an entity.
 */
typealias PropertyColumRelations<T> = Set<PropertyColumRelation<T, *>>
