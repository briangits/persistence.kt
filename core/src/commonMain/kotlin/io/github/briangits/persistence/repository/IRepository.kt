package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.pagination.Paginated

/**
 * Core interface defining standard Create, Read, Update, and Delete (CRUD) operations
 * for a domain entity.
 *
 * @param T The domain entity type.
 * @param TCreate The input type used for creating new instances of the entity.
 * @param TFilters The type-safe filter DSL used for querying the entity.
 */
interface IRepository<T : Any, TCreate : Any, TFilters : Filters<T, TFilters>> {
    /**
     * Provides a factory for creating a new instance of the filter builder [TFilters].
     */
    val filter: () -> TFilters

    /**
     * Defines a filtering strategy to uniquely identify an entity.
     * 
     * This is typically used for resolving primary keys or unique identifiers during 
     * updates or deletions.
     */
    val id: TFilters.(T) -> Unit

    /**
     * Calculates the total number of entities that match the criteria defined in the [block].
     *
     * @param block A builder lambda to configure query filters.
     * @return The total count of matching records.
     */
    suspend fun count(block: FilterBuilder<TFilters> = {}): Long

    /**
     * Determines if at least one entity matches the criteria defined in the [block].
     *
     * @param block A builder lambda to configure query filters.
     * @return `true` if any match is found; `false` otherwise.
     */
    suspend fun exists(block: FilterBuilder<TFilters> = {}): Boolean

    /**
     * Retrieves the first entity that matches the criteria defined in the [block].
     *
     * @param block A builder lambda to configure query filters.
     * @return The matching entity instance, or `null` if no record is found.
     */
    suspend fun find(block: FilterBuilder<TFilters> = {}): T?

    /**
     * Retrieves all entities that match the criteria defined in the [block].
     *
     * @param block A builder lambda to configure query filters.
     * @return A list containing all matching entity instances.
     */
    suspend fun findAll(block: FilterBuilder<TFilters> = {}): List<T>

    /**
     * Retrieves a paginated subset of entities matching the criteria defined in the [block].
     *
     * @param pagination Parameters defining the offset and limit for the result set.
     * @param block A builder lambda to configure query filters.
     * @return A [Paginated] object containing the result items and metadata.
     */
    suspend fun findAll(
        pagination: Pagination,
        block: FilterBuilder<TFilters> = {}
    ): Paginated<T>

    /**
     * Initializes a new entity instance from the provided creation parameters.
     *
     * @param create The input parameters required to construct the entity.
     * @return A new instance of the entity [T].
     */
    fun create(create: TCreate): T

    /**
     * Synchronizes the state of the provided [entity] with the persistent store.
     *
     * @param entity The entity instance to be updated or persisted.
     * @return The resulting entity state after synchronization.
     */
    suspend fun save(entity: T): T

    /**
     * Permanently removes the provided [entity] from the persistent store.
     *
     * @param entity The entity instance to delete.
     */
    suspend fun delete(entity: T)
}
