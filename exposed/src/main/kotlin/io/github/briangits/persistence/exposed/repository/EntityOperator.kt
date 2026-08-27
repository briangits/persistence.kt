package io.github.briangits.persistence.exposed.repository

import io.github.briangits.persistence.exposed.relations.PropertyColumRelations
import io.github.briangits.persistence.exposed.relations.RelationsBuilder
import io.github.briangits.persistence.exposed.relations.RelationsBuilderImpl
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table

open class EntityOperator<TTable : Table, T : Any>(
    open val fromDB: TTable.(ResultRow) -> T,
    relations: RelationsBuilder<T>.() -> Unit
) {
    val relations: PropertyColumRelations<T> by lazy {
        RelationsBuilderImpl<T>().apply(relations).build()
    }
}
