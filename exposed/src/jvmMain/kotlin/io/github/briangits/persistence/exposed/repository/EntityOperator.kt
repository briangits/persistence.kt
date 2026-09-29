package io.github.briangits.persistence.exposed.repository

import io.github.briangits.persistence.exposed.relations.PropertyColumnRelations
import io.github.briangits.persistence.query.properties.property.AnyProperty
import org.jetbrains.exposed.v1.core.ResultRow

interface EntityOperator<T : Any> {
    val fromDB: (ResultRow) -> T

    val relations: PropertyColumnRelations

    operator fun <T> ResultRow.get(path: AnyProperty<T>): T =
        this[relations[path]]

    fun fromDB(block: (ResultRow) -> T): (ResultRow) -> T =
        block

}
