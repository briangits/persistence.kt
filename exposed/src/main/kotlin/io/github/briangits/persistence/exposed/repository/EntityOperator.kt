package io.github.briangits.persistence.exposed.repository

import io.github.briangits.persistence.exposed.relations.PropertyColumRelations
import io.github.briangits.persistence.exposed.relations.RelationsBuilder
import io.github.briangits.persistence.exposed.relations.RelationsBuilderImpl
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table

/**
 * Responsible for mapping database [ResultRow]s to domain entities [T], and managing
 * the [PropertyColumRelations] for a specific entity type [T] and its associated [TTable].
 *
 * @param TTable The Exposed table implementation.
 * @param T The domain entity type.
 * @property fromDB Function to map an Exposed [ResultRow] to the domain entity [T].
 * @property relations The set of defined property-to-column mappings, lazily initialized.
 */
open class EntityOperator<TTable : Table, T : Any>(
    open val fromDB: TTable.(ResultRow) -> T,
    relations: RelationsBuilder<T>.() -> Unit
) {
    val relations: PropertyColumRelations<T> by lazy {
        RelationsBuilderImpl<T>().apply(relations).build()
    }
}
