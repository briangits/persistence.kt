package domain

import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.filters.Filters

class UserFilters : UserProperties(), Filters<User, UserFilters>

fun createFilters(builder: FilterBuilder<UserFilters>): UserFilters =
    UserFilters().apply(builder)
