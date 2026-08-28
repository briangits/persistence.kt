package repository

import closeDB
import createDB
import domain.NewUser
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.repository.create
import io.github.briangits.persistence.repository.save
import org.junit.Test
import runInTransaction
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class ExistsTests {
    @Test
    fun `exists returns true for matching entity`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val user = repository.save { create { NewUser("User1", null, 20) } }

            assertTrue(
                repository.exists {
                    id eq user.id
                }
            )

            closeDB(db)
        }

    @Test
    fun `exists returns false for non matching entity`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            assertFalse(
                repository.exists {
                    id eq Uuid.random()
                }
            )

            closeDB(db)
        }
}