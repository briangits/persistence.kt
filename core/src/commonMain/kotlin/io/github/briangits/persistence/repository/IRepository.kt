package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.pagination.Paginated

interface IRepository<T : Any, TCreate : Any, TFilters : Filters<T, TFilters>> {
    val filter: () -> TFilters
    val id: TFilters.(T) -> Unit

    suspend fun count(block: FilterBuilder<TFilters> = {}): Long

    suspend fun exists(block: FilterBuilder<TFilters> = {}): Boolean

    suspend fun find(block: FilterBuilder<TFilters> = {}): T?

    suspend fun findAll(block: FilterBuilder<TFilters> = {}): List<T>

    suspend fun findAll(
        pagination: Pagination,
        block: FilterBuilder<TFilters> = {}
    ): Paginated<T>

    fun create(create: TCreate): T

    suspend fun save(entity: T): T

    suspend fun delete(entity: T)
}

suspend fun <
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>
> IRepository<T, TCreate, TFilters>.findAll(
    offset: Long = 0,
    limit: Int? = null,
    block: FilterBuilder<TFilters> = {}
) = findAll(Pagination(offset, limit), block)

fun <
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>
> IRepository<T, TCreate, TFilters>.create(
    block: () -> TCreate
) = create(block())

suspend fun <
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>,
    TRepository : IRepository<T, TCreate, TFilters>
> TRepository.save(
    block: TRepository.() -> T
): T = save(block())

suspend fun <
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>,
    TRepository : IRepository<T, TCreate, TFilters>
> TRepository.delete(
    block: TRepository.() -> T
) = delete(block())
