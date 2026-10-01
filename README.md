<h1 align="center">persistence.kt</h1>

<p align="center">A persistence abstraction layer for Kotlin Multiplatform.</p>

[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
![Maven Central](https://img.shields.io/maven-central/v/io.github.briangits.persistence/core?color=blue)
![CI](https://github.com/briangits/persistence.kt/actions/workflows/ci.yaml/badge.svg)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)


`persistence.kt` provides a unified API for querying and persisting data,
without coupling your domain layer to a specific database framework.

---

## Quick Start

### 1. Add the dependency

```kotlin
dependencies {
    implementation("io.github.briangits.persistence:core:<version>")
}
```

### 2. Define your domain

```kotlin
import kotlin.uuid.Uuid

data class User(
    val id: Uuid = Uuid.random(),
    val name: String,
    val email: String?
)
```

Define properties to be used in queries when interacting with `User`:

```kotlin
import io.github.briangits.persistence.query.properties.Properties

open class UserProperties : Properties<User, UserProperties>() {
    val id by User::id
    val name by User::name
    val email by User::email

    fun withCompanyEmail() { email endsWith "@company.com" }
}
```

Then define a repository:

```kotlin
import io.github.briangits.persistence.repository.Repository

abstract class UserRepository : Repository<User, UserProperties>(
    id = { id eq it.id },
    properties = ::UserProperties
)
```

### 3. Saving and querying entities

```kotlin
import io.github.briangits.persistence.Persistence
import io.github.briangits.persistence.repository.get
import io.github.briangits.persistence.repository.save

suspend fun registerUser(persistence: Persistence) = persistence.transaction {
    val users = get<UserRepository>()

    users.save {
        User(name = "Jane Doe", email = "jane@company.com")
    }

    users.findAll {
        name startsWith "Jane"
        withCompanyEmail()
    }
}
```
---

## Queries

### Filters

Filters use the defined properties to provide a type safe DSL filtering entities.
Multiple conditions in the top level block are combined with `AND`.

| Operation          | Syntax                                                               |
|:-------------------|:---------------------------------------------------------------------|
| Equality           | `prop eq value`, `prop neq value`                                    |
| Comparison         | `prop gt value`, `prop gte value`, `prop lt value`, `prop lte value` |
| Range              | `prop.between(start, end)`                                           |
| Text               | `prop contains text`, `prop startsWith text`, `prop endsWith text`   |
| Regular expression | `prop matches pattern`                                               |
| Membership         | ``prop `in` values``, `prop notIn values`                            |
| Nullability        | `prop.isNull()`, `prop.isNotNull()`                                  |

Comparing a nullable property with `null` using `eq` or `neq` produces a null check.
Use `allOf`, `oneOf`, and `not` to group conditions:

```kotlin
users.findAll {
    oneOf {
        name startsWith "Jane"
        name startsWith "John"
    }
    not { email.isNull() }
}
```

### Nested properties

For nested values, define an `explode` object in your properties class.
For example, if `User.name` is a `PersonName` with `first` and `last` properties:

```kotlin
import io.github.briangits.persistence.query.properties.property.explode

open class UserProperties {
    /* Other properties */
    
    object name : explode<User, PersonName>(User::name) {
        val first by PersonName::first
        val last by PersonName::last
    }
}
```

Using the exploded object:

```kotlin
users.find {
    name.first eq "Jane"
    
    // OR
    
    explode(name) {
        first eq "Jane"
        last eq "Doe"
    }
}
```

### Pagination

Request a page with an offset and limit. The result contains `items`, `total`,
`offset`, and `limit`.

```kotlin
import io.github.briangits.persistence.repository.findAll

val page = users.findAll(offset = 0, limit = 20) {
    withCompanyEmail()
}
```

---

## Unit of Work

Use `UnitOfWork` to group repository operations and register hooks around commit.

```kotlin
dependencies {
    implementation("io.github.briangits.persistence:uow:<version>")
}
```

Define a unit of work with the repositories an operation needs:

```kotlin
import io.github.briangits.persistence.repository.get
import io.github.briangits.persistence.transaction.Transaction
import io.github.briangits.persistence.uow.UnitOfWork

class UserUoW(transaction: Transaction) : UnitOfWork<UserWork>(transaction) {
    val users = get<UserRepository>()
}
```

`run` executes the block and commits. `execute` lets you perform operations before
committing explicitly. Both roll back when their block fails.

```kotlin
suspend fun saveUser(persistence: Persistence, user: User) {
    UserUoW(persistence.createTransaction()).run {
        users.save(user)

        beforeCommit { check(users.exists { id eq user.id }) }
        afterCommit { println("Saved user ${user.id}") }
    }
}
```

---

## Adapters

The core API and unit-of-work module support Kotlin Multiplatform. Adapters provide
the database-specific repository and transaction implementations.

- **[Exposed](docs/exposed)** — JDBC persistence on JVM using JetBrains Exposed.

## License

[Apache-2.0](LICENSE)
