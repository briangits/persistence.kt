package io.github.briangits.persistence.repository

import io.github.briangits.persistence.query.Pagination
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.pagination.Paginated
import io.github.briangits.persistence.query.properties.Properties

interface IRepository<T : Any, TCreate : Any, TProperties : Properties<T, TProperties>> {

    val id: TProperties.(T) -> Unit

    val properties: () -> TProperties

    suspend fun count(block: FilterBuilder<TProperties> = {}): Long

    suspend fun exists(block: FilterBuilder<TProperties> = {}): Boolean

    suspend fun find(block: FilterBuilder<TProperties> = {}): T?

    suspend fun findAll(block: FilterBuilder<TProperties> = {}): List<T>

    suspend fun findAll(
        pagination: Pagination,
        block: FilterBuilder<TProperties> = {}
    ): Paginated<T>

    fun create(create: TCreate): T

    suspend fun save(entity: T): T

    suspend fun delete(entity: T)
}
