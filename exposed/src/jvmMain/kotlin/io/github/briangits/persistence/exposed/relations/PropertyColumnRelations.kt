package io.github.briangits.persistence.exposed.relations

import io.github.briangits.persistence.query.properties.property.AnyProperty
import io.github.briangits.persistence.query.properties.property.BaseProperty
import org.jetbrains.exposed.v1.core.Column

class PropertyColumnRelations private constructor(
    private val relations: MutableMap<BaseProperty<*, *>, Column<*>>
) {

    constructor(
        block: PropertyColumnRelations.() -> Unit
    ) : this(relations = mutableMapOf()) {
        block()
    }

    infix fun <T> AnyProperty<T>.mapsTo(column: Column<T>) {
        relations[this] = column
    }

    infix fun <T> AnyProperty<T>.by(column: Column<T>) = mapsTo(column)

    internal operator fun iterator(): Iterator<PropertyColumnRelation<*>> =
        relations.toList().iterator()

    @Suppress("UNCHECKED_CAST")
    internal operator fun <R : Any, T> get(path: BaseProperty<R, T>): Column<T> =
        relations[path] as? Column<T>
            ?: error("No column found for path $path")

}

fun relations(block: PropertyColumnRelations.() -> Unit): PropertyColumnRelations =
    PropertyColumnRelations(block)
