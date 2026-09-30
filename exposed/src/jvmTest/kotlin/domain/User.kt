package domain

import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val name: PersonName,
    val email: String?,
    val age: Int
) {

    constructor(
        name: PersonName,
        email: String?,
        age: Int
    ) : this(
        id = Uuid.random(),
        name = name,
        email = email,
        age = age
    )

}
