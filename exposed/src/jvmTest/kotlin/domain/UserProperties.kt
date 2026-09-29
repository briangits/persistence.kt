package domain

import io.github.briangits.persistence.query.properties.Properties
import io.github.briangits.persistence.query.properties.property.explode

open class UserProperties : Properties<User, UserProperties>() {
    val id by User::id

    object name : explode<User, PersonName>(User::name) {
        val first by PersonName::first
        val last by PersonName::last
    }

    val email by User::email

    val age by User::age
}
