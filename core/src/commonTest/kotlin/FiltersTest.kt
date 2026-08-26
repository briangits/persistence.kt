import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.filters.operators.Between
import io.github.briangits.persistence.query.filters.operators.Contains
import io.github.briangits.persistence.query.filters.operators.EndsWith
import io.github.briangits.persistence.query.filters.operators.Eq
import io.github.briangits.persistence.query.filters.operators.GroupOperator
import io.github.briangits.persistence.query.filters.operators.Gt
import io.github.briangits.persistence.query.filters.operators.Gte
import io.github.briangits.persistence.query.filters.operators.In
import io.github.briangits.persistence.query.filters.operators.IsNotNull
import io.github.briangits.persistence.query.filters.operators.IsNull
import io.github.briangits.persistence.query.filters.operators.Like
import io.github.briangits.persistence.query.filters.operators.Lt
import io.github.briangits.persistence.query.filters.operators.Lte
import io.github.briangits.persistence.query.filters.operators.Matches
import io.github.briangits.persistence.query.filters.operators.NEq
import io.github.briangits.persistence.query.filters.operators.Not
import io.github.briangits.persistence.query.filters.operators.NotIn
import io.github.briangits.persistence.query.filters.operators.OneOf
import io.github.briangits.persistence.query.filters.operators.StartsWith
import kotlin.test.Test
import kotlin.test.assertEquals

data class User(
    val id: String,
    val name: String,
    val email: String?,
    val age: Int,
    val friends: List<String>
)

fun createFilters(builder: FilterBuilder<UserFilters>): AllOf<User> =
    UserFilters().apply(builder).build()

class UserFilters : Filters<User, UserFilters>(::UserFilters) {
    val id = User::id
    val name = User::name
    val email = User::email
    val age = User::age
}

class FiltersTest {
    @Test
    fun `equality operators`() {
        val filters = createFilters {
            id eq "123"
            name neq "Jane Doe"
            email neq null
            email neq "johndoe@gmail.com"
            age eq 25
        }

        val expected = AllOf<User>(
            listOf(
                Eq(User::id, "123"),
                NEq(User::name, "Jane Doe"),
                IsNotNull(User::email),
                NEq(User::email, "johndoe@gmail.com"),
                Eq(User::age, 25)
            )
        )

        assertEquals(expected, filters)
    }

    @Test
    fun `comparison operators`() {
        val filters = createFilters {
            age gt 20
            age gte 18
            age lt 30
            age lte 35
            age.between(18, 65)

            name gt "A"
            email lte "c"
            name.between("B", "C")
        }

        val expected = AllOf<User>(
            listOf(
                Gt(User::age, 20),
                Gte(User::age, 18),
                Lt(User::age, 30),
                Lte(User::age, 35),
                Between(User::age, 18, 65),

                Gt(User::name, "A"),
                Lte(User::email, "c"),
                Between(User::name, "B", "C")
            )
        )

        assertEquals(expected, filters)
    }

    @Test
    fun `string operators`() {
        val filters = createFilters {
            name like "Jo%"
            name contains "John"
            name startsWith "John"
            name endsWith "Doe"
            name matches "John.*"
            name matches Regex("John.*")
        }

        val expected = AllOf<User>(
            listOf(
                Like(User::name, "Jo%"),
                Contains(User::name, "John"),
                StartsWith(User::name, "John"),
                EndsWith(User::name, "Doe"),
                Matches(User::name, "John.*"),
                Matches(User::name, Regex("John.*").pattern)
            )
        )

        assertEquals(expected, filters)
    }

    @Test
    fun `null operators`() {
        val filters = createFilters {
            email.isNull()
            email.isNotNull()
        }

        val expected = AllOf<User>(
            listOf(
                IsNull(User::email),
                IsNotNull(User::email)
            )
        )

        assertEquals(expected, filters)
    }

    @Test
    fun `array operators`() {
        val filters = createFilters {
            id `in` listOf("1", "2")
            id notIn listOf("3", "4")
        }

        val expected = AllOf<User>(
            listOf(
                In(User::id, listOf("1", "2")),
                NotIn(User::id, listOf("3", "4"))
            )
        )

        assertEquals(expected, filters)
    }

    @Test
    fun `logical operators`() {
        val filters = createFilters {
            allOf {
                age gt 20
                age lt 30
            }
            oneOf {
                id eq "1"
                id eq "2"
            }
            not {
                id eq "3"
            }
        }

        val expected = AllOf<User>(
            listOf<GroupOperator<User>>(
                AllOf(listOf(Gt(User::age, 20), Lt(User::age, 30))),
                OneOf(listOf(Eq(User::id, "1"), Eq(User::id, "2"))),
                Not(listOf(Eq(User::id, "3")))
            )
        )

        assertEquals(expected, filters)
    }
}