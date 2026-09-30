package io.github.briangits.persistence.exposed.repository

import io.github.briangits.persistence.exposed.query.filters.compile
import io.github.briangits.persistence.exposed.relations.set
import io.github.briangits.persistence.exposed.transaction.TransactionControl
import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.pagination.Paginated
import io.github.briangits.persistence.query.properties.Properties
import io.github.briangits.persistence.repository.BaseRepository
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert

interface ExposedRepository<
    TTable : Table,
    T : Any,
    TProperties : Properties<T, TProperties>
> : BaseRepository<T, TProperties> {
    val table: TTable
    val operator: EntityOperator<T>

    val transaction: TransactionControl

    private fun TProperties.compile() = this.compile(operator.relations, properties)
    private fun FilterBuilder<TProperties>.compile() = properties().apply(this).compile()

    override suspend fun count(block: FilterBuilder<TProperties>): Long =
        transaction.execute {
            table.selectAll()
                .where { block.compile() }
                .count()
        }

    override suspend fun exists(block: FilterBuilder<TProperties>): Boolean =
        transaction.execute {
            !table.selectAll()
                .where { block.compile() }
                .empty()
        }

    override suspend fun find(block: FilterBuilder<TProperties>): T? =
        transaction.execute {
            table.selectAll()
                .where { block.compile() }
                .limit(1)
                .singleOrNull()
                ?.let { operator.fromDB(it) }
        }

    override suspend fun findAll(block: FilterBuilder<TProperties>): List<T> =
        transaction.execute {
            table.selectAll()
                .where { block.compile() }
                .map { operator.fromDB(it) }
        }

    override suspend fun findAll(
        pagination: Pagination,
        block: FilterBuilder<TProperties>
    ): Paginated<T> =
        transaction.execute {
            val offset = pagination.offset
            val limit = pagination.limit

            val filters = block.compile()

            val query = table.select(table.columns)
                .where { filters }
                .offset(offset)

            val limitedQuery = if (limit != null) query.limit(limit) else query

            val items = limitedQuery.map { operator.fromDB(it) }
            val total = when {
                limit == null -> items.size.toLong()
                items.size < limit && items.isNotEmpty() -> offset + items.size.toLong()
                else -> table.selectAll()
                    .where { filters }
                    .count()
            }

            Paginated(offset, limit, total, items)
        }

    override suspend fun save(entity: T): T {
        transaction.execute {
            table.upsert {
                for (relation in operator.relations) {
                    relation.set(it, entity)
                }
            }
        }

        return entity
    }

    override suspend fun delete(entity: T) {
        transaction.execute {
            val filter = properties().apply { id(this, entity) }
            table.deleteWhere(limit = 1) { filter.compile() }
        }
    }
}
