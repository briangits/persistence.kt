package domain

data class User(
    val id: Long,
    val name: String,
    val email: String?,
    val age: Int
)
