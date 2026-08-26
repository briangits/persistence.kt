package infrustructure

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Users : LongIdTable("Users") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).nullable()
    val age = integer("age")
}
