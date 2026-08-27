
import domain.User
import domain.createFilters
import infrustructure.Users
import io.github.briangits.persistence.exposed.filters.compile
import io.github.briangits.persistence.exposed.relations.PropertyColumRelation
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

class FilterCompilerTests {
    val relations = setOf(
        PropertyColumRelation(User::id, Users.id),
        PropertyColumRelation(User::name, Users.name),
        PropertyColumRelation(User::email, Users.email),
        PropertyColumRelation(User::age, Users.age)
    )

    val db = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
        driver = Driver::class.qualifiedName!!
    )

    fun runTestInTransaction(test: () -> Unit) = transaction(db) { test() }

    @Test
    fun `equality operators`() = runTestInTransaction {
        val filters = createFilters {
            name eq "John"
            age neq 30
        }

        val op = filters compile relations

        val expected = Op.TRUE
            .and(Users.name eq "John")
            .and(Users.age neq 30)

        assertEquals(expected.toString(), op.toString())
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

        val op = filters compile relations

        val expected = Op.TRUE
            .and(Users.age greater 20)
            .and(Users.age greaterEq 18)
            .and(Users.age less 40)
            .and(Users.age lessEq 35)
            .and(Users.age.between(18, 65))

        assertEquals(expected.toString(), op.toString())
    }

    @Test
    fun `string operators`() = runTestInTransaction {
        val filters = createFilters {
            name like "Jo%"
            name contains "John"
            name startsWith "John"
            name endsWith "Doe"
            name matches "John.*"
        }

        val op = filters compile relations
        println(op.toString())
        val expected = Op.TRUE
            .and(Users.name like "Jo%")
            .and(
                Users.name like LikePattern.ofLiteral("").plus("%")
                    .plus(LikePattern.ofLiteral("John"))
                    .plus("%")
            ).and(Users.name like LikePattern.ofLiteral("John") + "%")
            .and(
                Users.name like LikePattern.ofLiteral("")
                    .plus("%")
                    .plus(LikePattern.ofLiteral("Doe"))
            ).and(Users.name regexp "John.*")

        assertEquals(expected.toString(), op.toString())
    }

    @Test
    fun `null operators`() = runTestInTransaction {
        val filters = createFilters {
            email.isNull()
            email.isNotNull()
        }

        val op = filters compile relations

        val expected = Op.TRUE
            .and(Users.email.isNull())
            .and(Users.email.isNotNull())

        assertEquals(expected.toString(), op.toString())
    }

    @Test
    fun `array operators`() = runTestInTransaction {
        val filters = createFilters {
            age `in` listOf(20, 30)
            age notIn listOf(10, 40)
        }

        val op = filters compile relations

        val expected = Op.TRUE
            .and(Users.age inList listOf(20, 30))
            .and(Users.age notInList listOf(10, 40))

        assertEquals(expected.toString(), op.toString())
    }

    @Test
    fun `logical operators`() = runTestInTransaction {
        val filters = createFilters {
            allOf {
                age gt 20
                age lt 30
            }
            oneOf {
                name eq "A"
                name eq "B"
            }
            not {
                name eq "C"
            }
        }

        val op = filters compile relations

        val expected = Op.TRUE
            .and(
                Op.TRUE
                    .and(Users.age greater 20)
                    .and(Users.age less 30)
            ).and(
                Op.FALSE
                    .or(Users.name eq "A")
                    .or (Users.name eq "B")
            ) and(
                not(
                    Op.TRUE
                        .and(Users.name eq "C")
                )
            )

        assertEquals(expected.toString(), op.toString())
    }
}
