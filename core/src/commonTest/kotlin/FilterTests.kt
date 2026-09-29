
import UserProperties.name
import io.github.briangits.persistence.query.filters.FilterBuilder
import io.github.briangits.persistence.query.filters.Filters
import io.github.briangits.persistence.query.filters.build
import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.filters.operators.Between
import io.github.briangits.persistence.query.filters.operators.Contains
import io.github.briangits.persistence.query.filters.operators.EndsWith
import io.github.briangits.persistence.query.filters.operators.Eq
import io.github.briangits.persistence.query.filters.operators.Gt
import io.github.briangits.persistence.query.filters.operators.Gte
import io.github.briangits.persistence.query.filters.operators.In
import io.github.briangits.persistence.query.filters.operators.IsNotNull
import io.github.briangits.persistence.query.filters.operators.IsNull
import io.github.briangits.persistence.query.filters.operators.Lt
import io.github.briangits.persistence.query.filters.operators.Lte
import io.github.briangits.persistence.query.filters.operators.Matches
import io.github.briangits.persistence.query.filters.operators.NEq
import io.github.briangits.persistence.query.filters.operators.Not
import io.github.briangits.persistence.query.filters.operators.NotIn
import io.github.briangits.persistence.query.filters.operators.OneOf
import io.github.briangits.persistence.query.filters.operators.Operator
import io.github.briangits.persistence.query.filters.operators.StartsWith
import io.github.briangits.persistence.query.properties.Properties
import io.github.briangits.persistence.query.properties.property.directPath
import io.github.briangits.persistence.query.properties.property.explode
import io.github.briangits.persistence.query.properties.property.nested
import kotlin.test.Test
import kotlin.test.assertEquals

data class PersonName(
    val first: String,
    val last: String
)

data class User(
    val id: String,
    val name: PersonName,
    val email: String?,
    val age: Int,
    val friends: List<String>
)

open class UserProperties : Properties<User>() {
    val id by User::id

    object name : explode<User, PersonName>(User::name) {
        val first by PersonName::first
        val last by PersonName::last
    }

    val email by User::email
    val age by User::age
    val friends by User::friends
}

class UserFilters :
    UserProperties(),
    Filters<User, UserFilters> {

    fun olderThan(age: Int) { this.age gt age }

}

fun createFilters(builder: FilterBuilder<UserFilters>): AllOf =
    UserFilters().apply(builder).build { UserFilters() }

class FilterTests {
    @Test
    fun `equality operators`() {
        val filters = createFilters {
            id eq "123"
            name.first neq "Jane"
            email neq "johndoe@gmail.com"

            olderThan(25)
        }

        val expected = AllOf(
            operators = listOf<Operator>(
                Eq(path = User::id.directPath(), value = "123"),
                NEq(
                    path = User::name.directPath()
                        .nested(PersonName::first),
                    value = "Jane"
                ),
                NEq(path = User::email.directPath(), value = "johndoe@gmail.com"),
                Gt(path = User::age.directPath(), value = 25)
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

            email lte "k"

            explode(name) {
                first gt "J"
                last.between("B", "E")
            }
        }

        val expected = AllOf(
            operators = listOf<Operator>(
                Gt(path = User::age.directPath(), value = 20),
                Gte(path = User::age.directPath(), value = 18),
                Lt(path = User::age.directPath(), value = 30),
                Lte(path = User::age.directPath(), value = 35),
                Between(path = User::age.directPath(), start = 18, end = 65),

                Lte(path = User::email.directPath(), value = "k"),

                Gt(
                    path = User::name.directPath()
                        .nested(PersonName::first),
                    value = "J"
                ),
                Between(
                    path = User::name.directPath()
                        .nested(PersonName::last),
                    start = "B",
                    end = "E"
                )
            )
        )

        assertEquals(expected, filters)
    }

    @Test
    fun `string operators`() {
        val filters = createFilters {
            name.first contains "John"
            name.first startsWith "John"
            name.last endsWith "Doe"

            explode(name) {
                first matches "John.*"
                first matches Regex("John.*")
            }
        }

        val expected = AllOf(
            operators = listOf<Operator>(
                Contains(
                    path = User::name.directPath()
                        .nested(PersonName::first),
                    value = "John"
                ),
                StartsWith(
                    path = User::name.directPath()
                        .nested(PersonName::first),
                    value = "John"
                ),
                EndsWith(
                    path = User::name.directPath()
                        .nested(PersonName::last),
                    value = "Doe"
                ),
                Matches(
                    path = User::name.directPath()
                        .nested(PersonName::first),
                    value = "John.*"
                ),
                Matches(
                    path = User::name.directPath()
                        .nested(PersonName::first),
                    value = Regex("John.*").pattern
                )
            )
        )

        assertEquals(expected, filters)
    }

    @Test
    fun `null operators`() {
        val filters = createFilters {
            email.isNull()
            email.isNotNull()

            email eq null
            email neq null
        }

        val expected = AllOf(
            operators = listOf(
                IsNull(path = User::email.directPath()),
                IsNotNull(path = User::email.directPath()),

                IsNull(path = User::email.directPath()),
                IsNotNull(path = User::email.directPath())
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

        val expected = AllOf(
            operators = listOf(
                In(path = User::id.directPath(), values = listOf("1", "2")),
                NotIn(path = User::id.directPath(), values = listOf("3", "4"))
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

        val expected = AllOf(
            operators = listOf(
                AllOf(
                    operators = listOf(
                        Gt(path = User::age.directPath(), value = 20),
                        Lt(path = User::age.directPath(), value = 30)
                    )
                ),
                OneOf(
                    operators = listOf(
                        Eq(path = User::id.directPath(), value = "1"),
                        Eq(path = User::id.directPath(), value = "2")
                    )
                ),
                Not(
                    operator = AllOf(
                        operators = listOf(
                            Eq(path = User::id.directPath(), value = "3")
                        )
                    )
                )
            )
        )

        assertEquals(expected, filters)
    }
}