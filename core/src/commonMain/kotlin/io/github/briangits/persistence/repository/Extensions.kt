package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.properties.Properties

suspend fun <
    T : Any,
    TProperties : Properties<T, TProperties>
> BaseRepository<T, TProperties>.findAll(
    offset: Long = 0,
    limit: Int? = null,
    block: FilterBuilder<TProperties> = {}
) = findAll(Pagination(offset, limit), block)

suspend fun <
    T : Any,
    TProperties : Properties<T, TProperties>,
    TRepository : BaseRepository<T, TProperties>
> TRepository.save(
    block: suspend TRepository.() -> T
): T = save(block())

suspend fun <
    T : Any,
    TProperties : Properties<T, TProperties>,
    TRepository : BaseRepository<T, TProperties>
> TRepository.delete(
    block: suspend TRepository.() -> T
) = delete(block())
