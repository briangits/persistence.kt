package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.properties.Properties

abstract class Repository<T : Any, TCreate : Any, TProperties : Properties<T, TProperties>>(
    override val id: TProperties.(T) -> Unit,
    override val properties: () -> TProperties,
    private val create: (create: TCreate) -> T
) : IRepository<T, TCreate, TProperties> {

    override fun create(create: TCreate): T = create.let(this.create)

}
