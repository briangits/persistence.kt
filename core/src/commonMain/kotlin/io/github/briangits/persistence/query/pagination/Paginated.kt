package io.github.briangits.persistence.query.pagination

/**
 * A container representing a discrete page of results from a larger data set.
 *
 * @param T The type of entity contained within the result set.
 * @param offset The number of records skipped before the first item in this result set.
 * @param limit The maximum number of records requested for this page.
 * @param total The absolute total count of records available across all pages.
 * @param items The collection of entity instances retrieved for the current page.
 */
data class Paginated<T>(
    val offset: Long,
    val limit: Int?,
    val total: Long,
    val items: List<T>
)
