package io.github.briangits.persistence.query

/**
 * Data model defining the window of data to retrieve from a query.
 *
 * @param offset The zero-based index of the first record to retrieve. Defaults to 0.
 * @param limit The maximum number of records to return.
 * If null, all records from the [offset] onwards are returned.
 */
data class Pagination(
    val offset: Long = 0,
    val limit: Int? = null
)
