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

class CountTests {
    @Test
    fun `count all entities`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            repository.save {
                User(
                    name = PersonName("User", "1"),
                    email = null,
                    age = 20
                )
            }
            repository.save {
                User(
                    name = PersonName("User", "2"),
                    email = "user2@example.com",
                    age = 25
                )
            }
            repository.save {
                User(
                    name = PersonName("User", "3"),
                    email = "user3@example.com",
                    age = 20
                )
            }

            assertEquals(3, repository.count())

            closeDB(db)
        }

    @Test
    fun `count filtered entities`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            repository.save {
                User(
                    name = PersonName("User", "1"),
                    email = null,
                    age = 20
                )
            }
            repository.save {
                User(
                    name = PersonName("User", "2"),
                    email = "user2@example.com",
                    age = 25
                )
            }
            repository.save {
                User(
                    name = PersonName("User", "3"),
                    email = "user3@example.com",
                    age = 25
                )
            }

            assertEquals(2, repository.count { age eq 25 })

            closeDB(db)
        }
}