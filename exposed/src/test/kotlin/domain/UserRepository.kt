package domain

import io.github.briangits.persistence.repository.Repository

abstract class UserRepository : Repository<User, NewUser, UserFilters>(
    id = { id eq it.id },
    filters = ::UserFilters,
    create = { User(it) }
)
