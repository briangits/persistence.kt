package io.github.briangits.persistence.exposed.repository

import io.github.briangits.persistence.exposed.relations.PropertyColumRelations
import io.github.briangits.persistence.exposed.relations.RelationsBuilder
import io.github.briangits.persistence.exposed.relations.RelationsBuilderImpl
import io.github.briangits.persistence.query.Filters
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.IdTable

class EntityOperator<TTable : IdTable<*>, T : Any, TFilters : Filters<T, TFilters>>(
    internal val fromDB: TTable.(ResultRow) -> T,
    internal val block: RelationsBuilder<T>.() -> Unit
) {
    internal val relations: PropertyColumRelations<T> by lazy {
        RelationsBuilderImpl<T>().apply(block).build()
    }
}
