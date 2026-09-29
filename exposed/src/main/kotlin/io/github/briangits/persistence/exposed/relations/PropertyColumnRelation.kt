package io.github.briangits.persistence.exposed.relations

import io.github.briangits.persistence.query.properties.property.AnyProperty
import io.github.briangits.persistence.query.properties.property.NestedProperty
import io.github.briangits.persistence.query.properties.property.Property
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.statements.UpsertStatement

typealias PropertyColumnRelation<T> = Pair<AnyProperty<T>, Column<T>>

@Suppress("UNCHECKED_CAST")
fun PropertyColumnRelation<*>.set(
    statement: UpsertStatement<Long>,
    value: Any
) {
    val path = first

    statement[this.second as Column<Any?>] =
        when (path) {
            is Property -> {
                val path = path as Property<Any, Any?>
                path.get(value)
            }
            is NestedProperty -> {
                val path = path as NestedProperty<Any, Any?>
                val parent = path.parent as Property<Any, Any?>

                parent.get(value)?.let { path.get(it) }
            }
        }
}
