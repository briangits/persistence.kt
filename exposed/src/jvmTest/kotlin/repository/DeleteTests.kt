package repository

import closeDB
import createDB
import domain.PersonName
import domain.User
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.repository.save
import org.junit.Test
import runInTransaction
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeleteTests {
    @Test
    fun `delete removes entity`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val user = repository.save {
                User(
                    name = PersonName("Jane", "Doe"),
                    email = "janedoe@example.com",
                    age = 20
                )
            }

            repository.delete(user)

            assertNull(
                repository.find {
                    id eq user.id
                }
            )

            closeDB(db)
        }

    @Test
    fun `delete only removes requested entity`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val user1 = repository.save {
                User(
                    name = PersonName("User", "1"),
                    email = null,
                    age = 20
                )
            }
            val user2 = repository.save {
                User(
                    name = PersonName("User", "2"),
                    email = null,
                    age = 20
                )
            }

            repository.delete(user1)

            assertNull(
                repository.find {
                    id eq user1.id
                }
            )

            assertEquals(user2, repository.find { id eq user2.id })

            closeDB(db)
        }
}