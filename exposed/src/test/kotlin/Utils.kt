
import infrustructure.Users
import io.github.briangits.persistence.exposed.transaction.TransactionControl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.h2.Driver
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager
import kotlin.uuid.Uuid

fun createDB(): Database {
    val name = Uuid.random().toString()

    val db = Database.connect(
        url = "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1;",
        driver = Driver::class.qualifiedName!!
    )

    transaction(db) {
        SchemaUtils.create(Users)
    }

    return db
}

fun closeDB(db: Database) {
    TransactionManager.closeAndUnregister(db)
}

private fun createTransactionControl(db: Database): TransactionControl =
    TransactionControl(
        transaction = db.transactionManager.newTransaction(),
        dispatcher = Dispatchers.IO
    )

fun runInTransaction(
    db: Database,
    test: suspend (control: TransactionControl, db: Database) -> Unit
) = runTest {
    val control = createTransactionControl(db)
    test(control, db)
    control.commit()
}
