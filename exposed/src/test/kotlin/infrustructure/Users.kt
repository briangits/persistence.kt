package infrustructure

import org.jetbrains.exposed.v1.core.Table

object Users : Table("Users") {
    val id = uuid("id").uniqueIndex()
    val firstName = varchar("firstName", 255)
    val lastName = varchar("lastName", 255)
    val email = varchar("email", 255).nullable()
    val age = integer("age")
}
