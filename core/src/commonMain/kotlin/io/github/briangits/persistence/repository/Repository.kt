package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Filters

/**
 * Abstract base implementation of [IRepository] providing default entity creation logic.
 *
 * @param T The domain entity type.
 * @param TCreate The input type used for creating new instances of the entity.
 * @param TFilter The type-safe filter DSL used for querying the entity.
 * @param filter A factory function to initialize a new [TFilter] instance.
 * @param id A DSL block used to identify a specific entity instance using filters.
 * @param create A mapping function that defines how to transform creation parameters [TCreate] into a domain entity [T].
 */
abstract class Repository<T : Any, TCreate : Any, TFilter : Filters<T, TFilter>>(
    override val filter: () -> TFilter,
    override val id: TFilter.(T) -> Unit,
    private val create: TCreate.() -> T
) : IRepository<T, TCreate, TFilter> {
    /**
     * Creates a new domain entity [T] by applying the provided [create] parameters 
     * to the internal mapping function.
     *
     * @param create The input parameters for entity creation.
     * @return The resulting domain entity instance.
     */
    override fun create(create: TCreate): T = create.run(this.create)
}

