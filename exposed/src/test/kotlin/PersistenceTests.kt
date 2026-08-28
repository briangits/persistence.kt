
import domain.NewUser
import domain.User
import domain.UserRepository
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.exposed.ExposedPersistence
import io.github.briangits.persistence.repository.create
import io.github.briangits.persistence.repository.get
import io.github.briangits.persistence.repository.save
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.Database
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ExposedPersistenceTest {
    private fun persistence(
        database: Database = createDB()
    ) = ExposedPersistence(
        database = database,
        registry = mapOf(
            UserRepository::class to ::ExposedUserRepository
        ),
        dispatcher = Dispatchers.IO
    )

    @Test
    fun `initializing persistence`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        assertFalse(persistence.initialization.isLocked)

        persistence.initialize()

        persistence.transaction {
            get<UserRepository>().save {
                create { NewUser("Jane Doe", "janedoe@example.com", 25) }
            }
        }

        persistence.close()
    }

    @Test
    fun `initialize() is idempotent`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        persistence.initialize()
        persistence.initialize()

        persistence.transaction {
            get<UserRepository>().save {
                create { NewUser("Jane Doe", "janedoe@example.com", 25) }
            }
        }

        persistence.close()
    }

    @Test
    fun `initializing persistence concurrently`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        coroutineScope {
            (1..10)
                .map {
                    async {
                        persistence.initialize()
                    }
                }
                .awaitAll()
        }

        persistence.transaction {
            get<UserRepository>().save {
                create { NewUser("Jane Doe", "janedoe@example.com", 25) }
            }
        }

        persistence.close()
    }

    @Test
    fun `closing persistence before initialize is a no-op`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        persistence.close()

        persistence.initialize()

        persistence.transaction {
            get<UserRepository>().save {
                create { NewUser("Jane Doe", "janedoe@example.com", 25) }
            }
        }

        persistence.close()
    }

    @Test
    fun `close() is idempotent`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        persistence.initialize()

        persistence.close()
        persistence.close()
    }

    @Test
    fun `creating transactions`() = runTest {
        val database = createDB()
        val persistence = persistence(database).also { it.initialize() }

        val transaction = persistence.createTransaction()

        assertNotNull(transaction)

        persistence.close()
    }

    @Test
    fun `transaction() auto-commits transactions`() = runTest {
        val database = createDB()
        val persistence = persistence(database).also { it.initialize() }

        val user = persistence.transaction {
            val repository = get<UserRepository>()

            return@transaction repository.save {
                create { NewUser("Jane Doe", "janedoe@gmail.com", 25) }
            }
        }

        val found = persistence.transaction {
            val repository = get<UserRepository>()

            return@transaction repository.find {
                id eq user.id
            }
        }

        assertEquals(user, found)

        persistence.close()
    }

    @Test
    fun `transaction() rolls back transactions on uncaught exceptions`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        persistence.initialize()

        var user: User? = null
        runCatching {
            persistence.transaction {
                val repository = get<UserRepository>()
                user = repository.save { create { NewUser("Jane Doe", "janedoe@example.com", 25) } }

                throw Exception("Transaction rollback test")
            }
        }

        persistence.transaction {
            val repository = get<UserRepository>()

            assertNull(
                repository.find {
                    id eq user!!.id
                }
            )
        }

        persistence.close()
    }
}
