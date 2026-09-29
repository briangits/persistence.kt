
import domain.NewUser
import domain.PersonName
import domain.User
import domain.UserRepository
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.exposed.ExposedPersistence
import io.github.briangits.persistence.repository.create
import io.github.briangits.persistence.repository.get
import io.github.briangits.persistence.repository.save
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.Database
import org.junit.Test
import kotlin.test.assertEquals
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
    fun `close() is idempotent`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        persistence.close()
        persistence.close()
    }

    @Test
    fun `creating transactions`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        val transaction = persistence.createTransaction()

        assertNotNull(transaction)

        persistence.close()
    }

    @Test
    fun `transaction() auto-commits transactions`() = runTest {
        val database = createDB()
        val persistence = persistence(database)

        val user = persistence.transaction {
            val repository = get<UserRepository>()

            return@transaction repository.save {
                create {
                    NewUser(
                        name = PersonName("Jane", "Doe"),
                        email = "janedoe@gmail.com",
                        age = 25
                    )
                }
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

        var user: User? = null
        runCatching {
            persistence.transaction {
                val repository = get<UserRepository>()
                user = repository.save {
                    create {
                        NewUser(
                            name = PersonName("Jane", "Doe"),
                            email = "janedoe@example.com",
                            age = 25
                        )
                    }
                }

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
