package repository

import closeDB
import createDB
import domain.NewUser
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.repository.create
import io.github.briangits.persistence.repository.save
import org.junit.Test
import runInTransaction
import kotlin.test.assertEquals

class SaveTests {
    @Test
    fun `saving an entity`() = runInTransaction(createDB()) { control, db ->
        val repository = ExposedUserRepository(control)

        val user = repository.save {
            create {
                NewUser(
                    name = "Jane Doe",
                    email = "janedoe@example.com",
                    age = 20
                )
            }
        }

        val saved = repository.find { id eq user.id }

        assertEquals(user, saved)

        closeDB(db)
    }

    @Test
    fun `saving existing entity updates entity`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val user = repository.save {
                create {
                    NewUser(
                        name = "Jane Doe",
                        email = "janedoe@example.com",
                        age = 20
                    )
                }
            }

            val updated = user.copy(
                name = "John Doe",
                email = "johndoe@example.com",
                age = 25
            )

            repository.save(updated)

            val found = repository.find { id eq user.id }

            assertEquals(updated, found)

            closeDB(db)
        }
}