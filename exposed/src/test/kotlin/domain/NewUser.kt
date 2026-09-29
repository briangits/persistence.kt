package domain

data class NewUser(
    val name: PersonName,
    val email: String?,
    val age: Int
)
