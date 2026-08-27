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
