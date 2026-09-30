package infrustructure

import domain.User
import domain.UserProperties
import domain.UserRepository
import io.github.briangits.persistence.exposed.repository.ExposedRepository
import io.github.briangits.persistence.exposed.transaction.TransactionControl

class ExposedUserRepository(
    override val transaction: TransactionControl
) : UserRepository(), ExposedRepository<Users, User, UserProperties> {
    override val table = Users
    override val operator = UserEntityOperator
}
