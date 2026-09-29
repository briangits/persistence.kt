package domain

import io.github.briangits.persistence.repository.Repository

abstract class UserRepository : Repository<User, NewUser, UserProperties>(
    id = { id eq it.id },
    properties = ::UserProperties,
    create = { User(it) }
)
