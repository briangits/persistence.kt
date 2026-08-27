package io.github.briangits.persistence.exposed.relations

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import kotlin.reflect.KProperty1

class PropertyColumRelation<T : Any, V>(
    val prop: KProperty1<T, V>,
    val column: Column<V>
) {
    fun set(
        builder: UpdateBuilder<Int>,
        entity: T
    ) {
        builder[column] = prop.get(entity)
    }
}

typealias PropertyColumRelations<T> = Set<PropertyColumRelation<T, *>>
