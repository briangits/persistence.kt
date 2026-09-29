package domain

import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val name: PersonName,
    val email: String?,
    val age: Int
) {

    constructor(user: NewUser) : this(
        id = Uuid.random(),
        name = user.name,
        email = user.email,
        age = user.age
    )

}
