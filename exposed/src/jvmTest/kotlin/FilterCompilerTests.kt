
import domain.UserProperties
import domain.UserProperties.name
import infrustructure.UserEntityOperator
import infrustructure.Users
import io.github.briangits.persistence.exposed.query.filters.compile
import io.github.briangits.persistence.query.properties.property.explode
import org.h2.Driver
import org.jetbrains.exposed.v1.core.LikePattern
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.between
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.not
import org.jetbrains.exposed.v1.core.notInList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.core.regexp
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

private fun createFilters(block: UserProperties.() -> Unit) =
    UserProperties().apply(block)
        .compile(UserEntityOperator.relations) { UserProperties() }

class FilterCompilerTests {
    val relations = UserEntityOperator.relations

    val db = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
        driver = Driver::class.qualifiedName!!
    )

    fun runTestInTransaction(test: () -> Unit) = transaction(db) { test() }

    @Test
    fun `equality operators`() = runTestInTransaction {
        val filters = createFilters {
            name.first eq "John"
            age neq 30
        }

        val expected = Op.TRUE
            .and(Users.firstName eq "John")
            .and(Users.age neq 30)

        assertEquals(expected.toString(), filters.toString())
    }

    @Test
    fun `comparison operators`() = runTestInTransaction {
        val filters = createFilters {
            age gt 20
            age gte 18
            age lt 40
            age lte 35
            age.between(18, 65)
        }

        val expected = Op.TRUE
            .and(Users.age greater 20)
            .and(Users.age greaterEq 18)
            .and(Users.age less 40)
            .and(Users.age lessEq 35)
            .and(Users.age.between(18, 65))

        assertEquals(expected.toString(), filters.toString())
    }

    @Test
    fun `string operators`() = runTestInTransaction {
        val filters = createFilters {
            explode(name) {
                first contains "John"
                first startsWith "John"
                first endsWith "Doe"
                first matches "John.*"
            }
        }

        val expected = Op.TRUE
            .and(
                Users.firstName like LikePattern.ofLiteral("").plus("%")
                    .plus(LikePattern.ofLiteral("John"))
                    .plus("%")
            ).and(Users.firstName like LikePattern.ofLiteral("John") + "%")
            .and(
                Users.firstName like LikePattern.ofLiteral("")
                    .plus("%")
                    .plus(LikePattern.ofLiteral("Doe"))
            ).and(Users.firstName regexp "John.*")

        assertEquals(expected.toString(), filters.toString())
    }

    @Test
    fun `null operators`() = runTestInTransaction {
        val filters = createFilters {
            email.isNull()
            email.isNotNull()
        }

        val expected = Op.TRUE
            .and(Users.email.isNull())
            .and(Users.email.isNotNull())

        assertEquals(expected.toString(), filters.toString())
    }

    @Test
    fun `array operators`() = runTestInTransaction {
        val filters = createFilters {
            age `in` listOf(20, 30)
            age notIn listOf(10, 40)
        }

        val expected = Op.TRUE
            .and(Users.age inList listOf(20, 30))
            .and(Users.age notInList listOf(10, 40))

        assertEquals(expected.toString(), filters.toString())
    }

    @Test
    fun `logical operators`() = runTestInTransaction {
        val filters = createFilters {
            allOf {
                age gt 20
                age lt 30
            }
            oneOf {
                name.first eq "A"
                name.last eq "B"
            }
            not {
                name.first eq "C"
            }
        }

        val expected = Op.TRUE
            .and(
                Op.TRUE
                    .and(Users.age greater 20)
                    .and(Users.age less 30)
            ).and(
                Op.FALSE
                    .or(Users.firstName eq "A")
                    .or (Users.lastName eq "B")
            ) and(
                not(
                    Op.TRUE
                        .and(Users.firstName eq "C")
                )
            )

        assertEquals(expected.toString(), filters.toString())
    }
}
