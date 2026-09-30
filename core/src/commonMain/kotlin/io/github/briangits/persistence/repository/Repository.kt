package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.properties.Properties

abstract class Repository<T : Any, TProperties : Properties<T, TProperties>>(
    override val id: TProperties.(T) -> Unit,
    override val properties: () -> TProperties
) : BaseRepository<T, TProperties>
