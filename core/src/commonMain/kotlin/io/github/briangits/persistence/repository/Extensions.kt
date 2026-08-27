package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder

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
