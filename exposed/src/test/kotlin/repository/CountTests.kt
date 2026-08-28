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

class CountTests {
    @Test
    fun `count all entities`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            repository.save { create { NewUser("User1", null, 20) } }
            repository.save { create { NewUser("User2", null, 25) } }
            repository.save { create { NewUser("User3", null, 30) } }

            assertEquals(3, repository.count())

            closeDB(db)
        }

    @Test
    fun `count filtered entities`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            repository.save { create { NewUser("User1", null, 20) } }
            repository.save { create { NewUser("User2", null, 25) } }
            repository.save { create { NewUser("User3", null, 25) } }

            assertEquals(2, repository.count { age eq 25 })

            closeDB(db)
        }
}