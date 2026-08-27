package domain

import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val name: String,
    val email: String?,
    val age: Int
)
