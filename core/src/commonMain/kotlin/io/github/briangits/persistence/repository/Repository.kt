package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.filters.Filters

abstract class Repository<T : Any, TCreate : Any, TFilter : Filters<T, TFilter>>(
    override val id: TFilter.(T) -> Unit,
    override val filters: () -> TFilter,
    private val create: (create: TCreate) -> T
) : IRepository<T, TCreate, TFilter> {

    override fun create(create: TCreate): T = create.let(this.create)

}
