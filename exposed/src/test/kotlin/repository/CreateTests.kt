package repository

import closeDB
import createDB
import domain.NewUser
import domain.PersonName
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.repository.create
import org.junit.Test
import runInTransaction
import kotlin.test.assertEquals

class CreateTests {
    @Test
    fun `creating entities`() = runInTransaction(createDB()) { control, db ->
        val repository = ExposedUserRepository(control)

        val user = repository.create {
            NewUser(
                name = PersonName("Jane", "Doe"),
                email = "janedoe@example.com",
                age = 20
            )
        }

        assertEquals(PersonName("Jane", "Doe"), user.name)
        assertEquals("janedoe@example.com", user.email)
        assertEquals(20, user.age)

        closeDB(db)
    }
}