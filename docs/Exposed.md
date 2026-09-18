# Implementing Persistence with Exposed

This guide explains how to implement the persistence API using [Exposed](https://www.jetbrains.com/help/exposed/home.html).

The Exposed adapter provides the database-specific implementations
for `Persistence`, `Transaction`, and `Repository`.

---

## Setup

Add the Exposed adapter to your project:

```kotlin
implementation("io.github.briangits.persistence:exposed:<version>")
```

You'll also need the appropriate Exposed JDBC and database driver dependencies.
See [Connecting to a Database](https://www.jetbrains.com/help/exposed/working-with-database.html#connecting-to-a-database) for a guide on setting this up.

---

## Configuring Persistence

Create an `ExposedPersistence` instance with an Exposed `Database`
and bind your repository implementations.

```kotlin
import io.github.briangits.persistence.Persistence
import io.github.briangits.persistence.exposed.ExposedPersistence
import org.jetbrains.exposed.v1.jdbc.Database

val database = Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
    driver = "org.h2.Driver"
)

val persistence: Persistence = ExposedPersistence(database) {
    bind<UserRepository>(::ExposedUserRepository)
}
```

The `bind` call tells the adapter which implementation should be used 
whenever `UserRepository` is requested.

Initialize the persistence layer when your application starts:

```kotlin
persistence.initialize()
```

---

## Define the Persistence Model

Domain models need to be mapped to the database's persistence models.

For example, given the following domain model:

```kotlin
data class User(
    val id: Uuid,
    val name: String,
    val email: String
)
```

Define the corresponding Exposed table:

```kotlin
import org.jetbrains.exposed.v1.core.Table

object UsersTable : Table("users") {
    val id = uuid("id").primaryKey()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
}
```

The table is an Exposed-specific representation of the domain model
and does not need to be exposed to the domain layer.

---

## Implementing Repositories

To implement a repository, extend `ExposedRepository` and provide:

1. The Exposed table.
2. An `EntityOperator` for mapping database rows to domain entities.
3. Accept a transaction which the repository will be bound to.

### Create an Entity Operator

`EntityOperator` defines how an Exposed result row is converted into a domain entity 
and how domain properties map to database columns.

```kotlin
import io.github.briangits.persistence.exposed.repository.EntityOperator

val userEntityOperator = EntityOperator<UsersTable, User>(
    fromDB = {
        User(
            id = it[UsersTable.id],
            name = it[UsersTable.name],
            email = it[UsersTable.email]
        )
    }
) {
    User::id mapsTo UsersTable.id
    User::name mapsTo UsersTable.name
    User::email mapsTo UsersTable.email
}
```

The `fromDB` function handles the database-to-domain mapping,
while the `mapsTo` declarations associate domain properties with their corresponding
table columns.

You can also define the operator as an `object`:

```kotlin
object UserEntityOperator : EntityOperator<UsersTable, User>(/* ... */)
```

---

### Implement the Repository

Now implement the repository interface defined by the domain layer:

```kotlin
import io.github.briangits.persistence.exposed.repository.ExposedRepository
import io.github.briangits.persistence.exposed.transaction.TransactionControl

class ExposedUserRepository(
    override val transaction: TransactionControl
) : UserRepository(), ExposedRepository<UsersTable, User, NewUser, UserFilters> {

    override val table: UsersTable = UsersTable

    override val operator: EntityOperator<UsersTable, User> =
        userEntityOperator
}
```

The `TransactionControl` is provided by the underlying `ExposedPersistence` implementation
when the repository is created.

---

## Using the Repository

Once the repository has been bound to `ExposedPersistence`,
it can then be resolved from a transaction:

```kotlin
suspend fun createUser(
    persistence: Persistence,
    newUser: NewUser
) {
    persistence.transaction {
        val users = get<UserRepository>()

        val user = users.create(newUser)

        users.save(user)
    }
}
```

---

## Complete Example

Putting everything together:

```kotlin
import io.github.briangits.persistence.Persistence
import io.github.briangits.persistence.exposed.ExposedPersistence
import io.github.briangits.persistence.exposed.repository.EntityOperator
import io.github.briangits.persistence.exposed.repository.ExposedRepository
import io.github.briangits.persistence.exposed.transaction.TransactionControl
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import java.util.UUID

// Domain layer

data class User(
    val id: Uuid,
    val name: String,
    val email: String
)

data class NewUser(
    val name: String,
    val email: String
)

class UserFilters : Filters<User, UserFilters>(::UserFilters) {
    val id = User::id
    val name = User::name
    val email = User::email
}

abstract class UserRepository : Repository<User, NewUser, UserFilters>(
    id = { id eq it.id },
    filters = ::UserFilters,
    create = { User(id = Uuid.randomUUID(), name, email) }
)

// Persistence model

object UsersTable : Table("users") {
    val id = uuid("id").primaryKey()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
}

// Entity mapping

val userEntityOperator = EntityOperator<UsersTable, User>(
    fromDB = {
        User(
            id = it[UsersTable.id],
            name = it[UsersTable.name],
            email = it[UsersTable.email]
        )
    }
) {
    User::id mapsTo UsersTable.id
    User::name mapsTo UsersTable.name
    User::email mapsTo UsersTable.email
}

// Repository implementation

class ExposedUserRepository(
    override val transaction: TransactionControl
) : UserRepository(), ExposedRepository<UsersTable, User, NewUser, UserFilters> {
    override val table = UsersTable
    override val operator = userEntityOperator
}

// Persistence configuration

val database = Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
    driver = "org.h2.Driver"
)

val persistence: Persistence = ExposedPersistence(database) {
    bind<UserRepository>(::ExposedUserRepository)
}

persistence.initialize()
```
