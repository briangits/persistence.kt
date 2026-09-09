# persistence

DDD-oriented persistence abstraction for Kotlin.

`persistence` provides a unified API for accessing and persisting domain data 
while keeping your domain layer independent of the underlying persistence implementation.

It combines the **Repository** and **Unit of Work** patterns with a type-safe filtering DSL
allowing persistence implementations to be swapped without changing application or domain code.

---

## Table of Contents
- [Core Concepts](#core-concepts)
- [Quick Start](#quick-start)
- [Filter DSL](#filter-dsl)
- [Unit of Work](#unit-of-work)
- [Implementations](#implementations)

---

## Core Concepts

The library is built around a small set of abstractions:

- **Persistence** - The entry point for managing the persistence lifecycle,
    creating & executing transactions.
- **Repository** - Provides a standard CRUD API for a domain entity:
    count, exists, find, findAll, create, save, and delete.
- **Filters** - A type-safe DSL for constructing queries using Kotlin property references,
    such as User::name eq "John".
- **Transaction** - Represents an atomic unit of work and acts as a `RepositoryFactory` 
    for resolving repositories within the transaction.
- **UnitOfWork** - Coordinates multiple repositories and provides lifecycle hooks 
    such as beforeCommit and afterCommit.

The application and domain layers depend only on these abstractions.
Persistence implementations are provided separately through adapters.

## Quick Start

### Add the dependency

```kotlin
dependencies {
    implementation("io.github.briangits.persistence:persistence:<version>")
}
```

### 2. Define your Domain

Start with your domain models:

```kotlin
data class User(val id: String, val name: String, val email: String)
data class NewUser(val name: String, val email: String)
```

Create a type-safe filter DSL for `User`:

```kotlin
class UserFilters : Filters<User, UserFilters>(::UserFilters) {
    // Shorthands for accessing properries
    val id = User::id
    val name = User::name
    val email = User::email
    
    // You can add custom filter functions here
    fun nameIsJohn() = name eq "John"
}
```

Then define a repository:

```kotlin
interface UserRepository : Repository<User, NewUser, UserFilters>(
    // Define how a user is uniquely identified
    id = { id eq it.id },
    // Filters
    filters = ::UserFilters,
    // Creating a new User from NewUser
    create = { User(id = UUID.randomUUID().toString(), name, email) }
)
```

### 2. Basic CRUD Operations

Repositories provide a simple CRUD API for managing entities:

```kotlin
suspend fun userService(repo: UserRepository) {
    // Create & Save
    val newUser = repo.create { NewUser("Jane Doe", "janedoe@example.com") }
    repo.save(newUser)

    // Filters users with DSL
    val user = repo.find {
        email eq "janedoe@gmail.com"
    }

    // List all matches
    val bobs = repo.findAll {
        name startsWith "Bob"
    }
}
```

### 3. Transactional Context

Use `Persistence.transaction()` to execute operation atomically.
Transactions are auto-committed or rolled back when an exception is thrown.

The `block` has a `Transaction` as its receiver, allowing you to resolve repositories 
that are bound to the transaction. 

```kotlin
suspend fun registerUser(persistence: Persistence, newUser: NewUser) {
    persistence.transaction {
        val repo = get<UserRepository>() // Resolve a repository
        
        if (repo.exists { email eq newUser.email }) throw Exception("User already exists")
        
        repo.save { create(newUser) }
    }
}
```

You can also create & manually manage a transaction:

```kotlin
fun registerUser(newUser: NewUser) {
    val transaction = persistence.creqteTransaction()
    val repo = transaction.get<UserRepository>()
    
    try {
        if (repo.exists { email eq newUser.email }) throw Exception("User already exists")
        
        repo.save { create(newUser) }
        transaction.commit()
    } catch (e: Exception) {
        transaction.rollback()
        throw e
    }
}
```

---

## Filter DSL

`Filters` provides a type-safe DSL for constructing queries using Kotlin property references.

| Operator        | DSL Usage             | Description                                           |
|:----------------|:----------------------|:------------------------------------------------------|
| **Equality**    | `prop eq value`       | Property equals value (or `isNull` if value is null)  |
|                 | `prop neq value`      | Property not equals (or `isNotNull` if value is null) |
| **Comparison**  | `prop gt / gte value` | Greater than / Greater than or equal                  |
|                 | `prop lt / lte value` | Less than / Less than or equal                        |
|                 | `prop.between(a, b)`  | Value is between `a` and `b` (inclusive)              |
| **String**      | `prop contains str`   | Substring search                                      |
|                 | `prop startsWith str` | Prefix search                                         |
|                 | `prop endsWith str`   | Suffix search                                         |
|                 | `prop matches regex`  | Regular expression match                              |
| **Collections** | `prop in values`      | Value is in the provided collection                   |
|                 | `prop notIn values`   | Value is NOT in the provided collection               |
| **Nullability** | `prop.isNull()`       | Property is null                                      |
|                 | `prop.isNotNull()`    | Property is not null                                  |

### Logical Grouping

Filters cab be grouped using: 
- `allOf` - AND
- `oneOf` - OR
- `not` - NOT

Multiple expressions at the top level of a  filter block are implicitly grouped using `allOf`.

```kotlin
val users = repo.findAll {
    oneOf {
        name startsWith "Jane"
        
        allOf {
            email ends "@company.com"
            name endsWith "Doe"
        }
    }
}
```

---

## Unit of Work

`UnitOfWork` coordinates operations across multiple repositories 
and provides lifecycle hooks around transaction completion.

Add the dependency:

```kotlin
dependencies {
    implementation("io.github.briangits.persistence:uow:<version>")
}
```

Define a unit of work:

```kotlin
class MyUnitOfWork(transaction: Transaction) : UnitOfWork<MyUnitOfWork>(transaction) {
    val users = get<UserRepository>()
    val posts = get<PostRepository>()
    
    init {
        afterCommit { /* Do some cleanup */ }
    }
}
```

You can also register hooks for individual operations in th `execute()` or `run()` blocks:

```kotlin
suspend fun complexOperation(uow: MyUnitOfWork) {
    uow.run {
        val user = users.find { id eq "123" }
        
        // ... perform changes ...
        
        beforeCommit { 
            println("Preparing to commit changes for ${user?.name}")
        }
        
        afterCommit {
            println("Transaction successful!")
        }
    }
}
```

---

## Implementations

`persistence` is intentionally implementation-agnostic.

The core library defines the abstractions used by the domain and application layers,
while persistence adapters provide the actual database or storage implementation.

This allows the same application code to work with different persistence libraries/frameworks
without coupling the domain model to a specific ORM or database library.

Currently supported implementations include:
- **[Exposed](docs/Exposed.md)** - Database persistence for Kotlin/JVM using JetBrains Exposed.

Additional implementations can be provided without modifying the core persistence API. 

---

## License

[Apache-2.0](LICENSE)
