package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Filters

abstract class Repository<T : Any, TCreate : Any, TFilter : Filters<T, TFilter>>(
    override val filter: () -> TFilter,
    private val create: TCreate.() -> T
) : IRepository<T, TCreate, TFilter> {
    override fun create(create: TCreate): T = create.run(this.create)
}
