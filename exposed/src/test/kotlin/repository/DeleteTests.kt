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
import kotlin.test.assertNull

class DeleteTests {
    @Test
    fun `delete removes entity`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val user = repository.save { create { NewUser("User1", null, 20) } }

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

            val user1 = repository.save { create { NewUser("User1", null, 20) } }
            val user2 = repository.save { create { NewUser("User2", null, 25) } }

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