package repository

import closeDB
import createDB
import domain.NewUser
import domain.PersonName
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.repository.create
import io.github.briangits.persistence.repository.save
import org.junit.Test
import runInTransaction
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class FindTests {
    @Test
    fun `find returns matching entity`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val user = repository.save {
                create {
                    NewUser(
                        name = PersonName("User", "1"),
                        email = null,
                        age = 20
                    )
                }
            }

            val found = repository.find { id eq user.id }

            assertEquals(user, found)

            closeDB(db)
        }

    @Test
    fun `find returns first matching entity`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val user1 = repository.save {
                create {
                    NewUser(
                        name = PersonName("User", "1"),
                        email = null,
                        age = 20
                    )
                }
            }
            val user2 = repository.save {
                create {
                    NewUser(
                        name = PersonName("User", "2"),
                        email = null,
                        age = 20
                    )
                }
            }
            val user3 = repository.save {
                create {
                    NewUser(
                        name = PersonName("User", "3"),
                        email = null,
                        age = 20
                    )
                }
            }

            val found = repository.find { age eq 20 }

            assertEquals(user1, found)

            closeDB(db)
        }

    @Test
    fun `find non existent entity returns null`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val found = repository.find { id eq Uuid.random() /* What are the odds */ }

            assertNull(found)

            closeDB(db)
        }

    @Test
    fun `find all returns all matching entities`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)



            val user1 = repository.save {
                create {
                    NewUser(
                        name = PersonName("User", "1"),
                        email = null,
                        age = 20
                    )
                }
            }

            val user2 = repository.save {
                create {
                    NewUser(
                        name = PersonName("User", "2"),
                        email = null,
                        age = 20
                    )
                }
            }

            val user3 = repository.save {
                create {
                    NewUser(
                        name = PersonName("User", "3"),
                        email = null,
                        age = 20
                    )
                }
            }

            val found = repository.findAll { age eq 20 }

            assertEquals(3, found.size)
            assertEquals(listOf(user1, user2, user3), found)

            closeDB(db)
        }

    @Test
    fun `find all returns empty list when nothing matches`() =
        runInTransaction(createDB()) { control, db ->
            val repository = ExposedUserRepository(control)

            val found = repository.findAll { id eq Uuid.random() /* What are the odds */ }

            assertTrue(found.isEmpty())

            closeDB(db)
        }
}