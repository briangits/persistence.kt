package io.github.briangits.persistence.exposed.relations

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import kotlin.reflect.KProperty1

sealed class PropertyColumRelation<T : Any, V>(
    open val prop: KProperty1<T, V>
) {
    abstract fun set(
        builder: UpdateBuilder<Int>,
        entity: T
    )

    data class Id<T : Any, V : Any>(
        override val prop: KProperty1<T, V>,
        val column: Column<EntityID<V>>
    ) : PropertyColumRelation<T, V>(prop) {
        override fun set(
            builder: UpdateBuilder<Int>,
            entity: T
        ) {
            @Suppress("UNCHECKED_CAST")
            builder[column] = prop.get(entity)
        }
    }

    data class Field<T : Any, V>(
        override val prop: KProperty1<T, V>,
        val column: Column<V>
    ) : PropertyColumRelation<T, V>(prop) {
        override fun set(
            builder: UpdateBuilder<Int>,
            entity: T
        ) {
            builder[column] = prop.get(entity)
        }
    }
}

typealias PropertyColumRelations<T> = Set<PropertyColumRelation<T, *>>
