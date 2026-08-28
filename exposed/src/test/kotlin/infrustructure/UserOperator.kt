package infrustructure

import domain.User
import io.github.briangits.persistence.exposed.repository.EntityOperator

object UserOperator : EntityOperator<Users, User>(
    fromDB = {
        User(
            id = it[id],
            name = it[name],
            email = it[email],
            age = it[age]
        )
    },
    relations = {
        User::id mapsTo Users.id
        User::name mapsTo Users.name
        User::email mapsTo Users.email
        User::age mapsTo Users.age
    }
)
