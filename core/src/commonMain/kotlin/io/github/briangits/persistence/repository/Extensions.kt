package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.properties.Properties

suspend fun <
    T : Any,
    TCreate : Any,
    TProperties : Properties<T, TProperties>
> IRepository<T, TCreate, TProperties>.findAll(
    offset: Long = 0,
    limit: Int? = null,
    block: FilterBuilder<TProperties> = {}
) = findAll(Pagination(offset, limit), block)

fun <
    T : Any,
    TCreate : Any,
    TProperties : Properties<T, TProperties>
> IRepository<T, TCreate, TProperties>.create(
    block: () -> TCreate
) = create(block())

suspend fun <
    T : Any,
    TCreate : Any,
    TProperties : Properties<T, TProperties>,
    TRepository : IRepository<T, TCreate, TProperties>
> TRepository.save(
    block: suspend TRepository.() -> T
): T = save(block())

suspend fun <
    T : Any,
    TCreate : Any,
    TProperties : Properties<T, TProperties>,
    TRepository : IRepository<T, TCreate, TProperties>
> TRepository.delete(
    block: suspend TRepository.() -> T
) = delete(block())
