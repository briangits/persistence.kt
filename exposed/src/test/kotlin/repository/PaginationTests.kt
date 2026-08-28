package repository

import closeDB
import createDB
import domain.NewUser
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.repository.create
import io.github.briangits.persistence.repository.findAll
import io.github.briangits.persistence.repository.save
import org.junit.Test
import runInTransaction
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaginationTests {
    @Test
    fun `pagination returns requested page`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            repeat(10) {
                repository.save {
                    create {
                        NewUser(
                            name = "User$it",
                            email = "user$it@example.com",
                            age = 20 + it
                        )
                    }
                }
            }

            val result = repository.findAll(offset = 0, limit = 3)

            assertEquals(3, result.items.size)
            assertEquals(0, result.offset)
            assertEquals(3, result.limit)
            assertEquals(10, result.total)

            closeDB(db)
        }

    @Test
    fun `pagination applies offset`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            repeat(10) {
                repository.save {
                    create {
                        NewUser(
                            name = "User$it",
                            email = null,
                            age = 20 + it
                        )
                    }
                }
            }

            val result = repository.findAll(offset = 5, limit = 3)

            assertEquals(3, result.items.size)
            assertEquals(5, result.offset)
            assertEquals(3, result.limit)
            assertEquals(10, result.total)

            closeDB(db)
        }

    @Test
    fun `pagination returns partial final page`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            repeat(5) {
                repository.save {
                    create {
                        NewUser(
                            name = "User$it",
                            email = null,
                            age = 20 + it
                        )
                    }
                }
            }

            val result = repository.findAll(offset = 3, limit = 5)

            assertEquals(2, result.items.size)
            assertEquals(3, result.offset)
            assertEquals(5, result.limit)
            assertEquals(5, result.total)

            closeDB(db)
        }

    @Test
    fun `pagination beyond result returns empty page`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            repeat(5) {
                repository.save {
                    create {
                        NewUser(
                            name = "User$it",
                            email = null,
                            age = 20 + it
                        )
                    }
                }
            }

            val result = repository.findAll(offset = 10, limit = 5)

            assertTrue(result.items.isEmpty())
            assertEquals(10, result.offset)
            assertEquals(5, result.limit)
            assertEquals(5, result.total)

            closeDB(db)
        }
}