import domain.NewUser
import domain.UserRepository
import infrustructure.ExposedUserRepository
import io.github.briangits.persistence.exposed.transaction.ExposedTransaction
import io.github.briangits.persistence.repository.create
import io.github.briangits.persistence.repository.get
import io.github.briangits.persistence.repository.save
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.Database
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TransactionTest {
    private fun createTransaction(db: Database): ExposedTransaction =
        ExposedTransaction(
            control = createTransactionControl(db),
            registry = mapOf(
                UserRepository::class to ::ExposedUserRepository
            )
        )

    @Test
    fun `data is persisted for committed transaction`() = runTest {
        val db = createDB()
        val transaction = createTransaction(db)

        val repository = transaction.get<UserRepository>()
        val user = repository.save { create { NewUser("Jane Doe", "janedoe@example.com", 25) } }

        transaction.commit()

        runInTransaction(db) { control, _ ->
            val repository = ExposedUserRepository(control)

            val found = repository.find { id eq user.id }

            assertEquals(user, found)
        }
    }

    @Test
    fun `data is not persisted for rolled-back transactions`() = runTest {
        val db = createDB()
        val transaction = createTransaction(db)

        val repository = transaction.get<UserRepository>()
        val user = repository.save { create { NewUser("Jane Doe", "janedoe@example.com", 25) } }

        transaction.rollback()

        runInTransaction(db) { control, _ ->
            val repository = ExposedUserRepository(control)

            assertNull(
                repository.find {
                    id eq user.id
                }
            )
        }
    }
}
