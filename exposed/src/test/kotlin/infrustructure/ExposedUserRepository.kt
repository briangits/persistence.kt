package infrustructure

import domain.NewUser
import domain.User
import domain.UserFilters
import domain.UserRepository
import io.github.briangits.persistence.exposed.repository.ExposedRepository
import io.github.briangits.persistence.exposed.transaction.TransactionControl

class ExposedUserRepository(
    override val transaction: TransactionControl
) : UserRepository(), ExposedRepository<Users, User, NewUser, UserFilters> {
    override val table = Users
    override val operator = UserEntityOperator
}
