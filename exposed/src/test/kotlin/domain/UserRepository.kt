package domain

import io.github.briangits.persistence.repository.Repository
import kotlin.uuid.Uuid

abstract class UserRepository : Repository<User, NewUser, UserFilters>(
    filter = ::UserFilters,
    id = { id eq it.id },
    create = {
        User(id = Uuid.random(), name, email, age)
    }
)
