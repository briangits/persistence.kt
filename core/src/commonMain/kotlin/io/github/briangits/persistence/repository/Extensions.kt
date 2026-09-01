package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder

/**
 * Finds a paginated subset of entities matching the criteria defined in the [block],
 * using [offset] and [limit].
 *
 * @param offset The number of entities to skip.
 * @param limit The maximum number of entities to return.
 * @param block The filter construction block.
 * @return A paginated result containing entities and pagination metadata.
 */
suspend fun <
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>
> IRepository<T, TCreate, TFilters>.findAll(
    offset: Long = 0,
    limit: Int? = null,
    block: FilterBuilder<TFilters> = {}
) = findAll(Pagination(offset, limit), block)

/**
 * Creates a new entity instance, using the creation parameters returned by [block].
 *
 * @param block A block that returns the creation parameters.
 * @return The created entity.
 */
fun <
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>
> IRepository<T, TCreate, TFilters>.create(
    block: () -> TCreate
) = create(block())

/**
 * Saves the entity returned by [block].
 *
 * @param block A block that returns the entity to save, with [TRepository] as its receiver.
 * @return The saved entity.
 */
suspend fun <
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>,
    TRepository : IRepository<T, TCreate, TFilters>
> TRepository.save(
    block: suspend TRepository.() -> T
): T = save(block())

/**
 * Deletes the entity returned by [block].
 *
 * @param block A block that returns the entity to be deleted, with [TRepository] as its receiver.
 */
suspend fun <
    T : Any,
    TCreate : Any,
    TFilters : Filters<T, TFilters>,
    TRepository : IRepository<T, TCreate, TFilters>
> TRepository.delete(
    block: suspend TRepository.() -> T
) = delete(block())
