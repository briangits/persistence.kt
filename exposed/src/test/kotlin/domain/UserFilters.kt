package domain

import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.filters.FilterBuilder

class UserFilters : Filters<User, UserFilters>(::UserFilters) {
    val id = User::id
    val name = User::name
    val email = User::email
    val age = User::age
}

fun createFilters(builder: FilterBuilder<UserFilters>): UserFilters =
    UserFilters().apply(builder)
