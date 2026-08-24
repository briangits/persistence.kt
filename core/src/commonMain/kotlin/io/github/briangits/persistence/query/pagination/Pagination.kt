package io.github.briangits.persistence.query

data class Pagination(
    val offset: Long = 0,
    val limit: Int? = null
)
