package io.github.briangits.persistence.transaction

import io.github.briangits.persistence.repository.RepositoryFactory

interface Transaction : RepositoryFactory {
    suspend fun commit()
    suspend fun rollback()
}
