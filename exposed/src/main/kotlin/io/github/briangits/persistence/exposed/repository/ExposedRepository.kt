package io.github.briangits.persistence.exposed.repository

import io.github.briangits.persistence.exposed.filters.compile
import io.github.briangits.persistence.exposed.transaction.TransactionControl
import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.pagination.Paginated
import io.github.briangits.persistence.repository.IRepository
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert

interface ExposedRepository<
    TTable : IdTable<*>,
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>
> : IRepository<T, TCreate, TFilters> {
    val table: TTable
    val operator: EntityOperator<TTable, T, TFilters>

    val transaction: TransactionControl

    private fun TFilters.compile() = this compile operator.relations
    private fun FilterBuilder<TFilters>.compile(): Op<Boolean> =
        filter().apply(this).compile()

    override suspend fun count(block: FilterBuilder<TFilters>): Long =
        transaction.execute {
            table
                .selectAll()
                .where { block.compile() }
                .count()
        }

    override suspend fun exists(block: FilterBuilder<TFilters>): Boolean =
        transaction.execute {
            !table
                .selectAll()
                .where { block.compile() }
                .empty()
        }

    override suspend fun find(block: FilterBuilder<TFilters>): T? =
        transaction.execute {
            table
                .selectAll()
                .where { block.compile() }
                .limit(1)
                .singleOrNull()
                ?.let { operator.fromDB(table, it) }
        }

    override suspend fun findAll(block: FilterBuilder<TFilters>): List<T> =
        transaction.execute {
            table
                .selectAll()
                .where { block.compile() }
                .map { operator.fromDB(table, it) }
        }

    override suspend fun findAll(
        pagination: Pagination,
        block: FilterBuilder<TFilters>
    ): Paginated<T> =
        transaction.execute {
            val offset = pagination.offset
            val limit = pagination.limit

            val totalCount =
                table.id
                    .count()
                    .over()
                    .alias("totalCount")
            val filters = block.compile()

            val query =
                table
                    .select(table.columns + totalCount)
                    .where { filters }
                    .offset(offset)

            val limitedQuery = if (limit != null) query.limit(limit) else query

            var total = 0L
            val items =
                limitedQuery.map {
                    total = it[totalCount]
                    operator.fromDB(table, it)
                }

            if (items.isEmpty() && offset > 0) {
                total =
                    table
                        .selectAll()
                        .where { filters }
                        .count()
            }

            Paginated(offset, limit, total, items)
        }

    override suspend fun save(entity: T) {
        transaction.execute {
            table.upsert {
                for (relation in operator.relations) {
                    relation.set(it, entity)
                }
            }
        }
    }

    override suspend fun delete(entity: T) {
        transaction.execute {
            val filter = filter().apply { id(this, entity) }
            table.deleteWhere(limit = 1) { filter.compile() }
        }
    }
}
