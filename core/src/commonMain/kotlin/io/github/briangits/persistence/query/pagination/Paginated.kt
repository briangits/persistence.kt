package io.github.briangits.persistence.query.pagination

data class Paginated<T>(
    val offset: Long,
    val limit: Int?,
    val total: Long,
    val items: List<T>
)
