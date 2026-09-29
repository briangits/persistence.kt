package io.github.briangits.persistence.exposed.repository

import io.github.briangits.persistence.exposed.transaction.TransactionControl
import io.github.briangits.persistence.repository.Repository
import io.github.briangits.persistence.repository.RepositoryFactory
import kotlin.reflect.KClass

typealias RepositoryImplementation<T> = (control: TransactionControl) -> T

internal typealias AnyRepository = Repository<*, *, *>
internal typealias RepositoryRegistry =
    Map<KClass<*>, RepositoryImplementation<AnyRepository>>

/**
 * Internal factory implementation responsible for creating and caching repository instances
 * within an active transactional context.
 *
 * Resolves repository implementations from the [RepositoryRegistry] and injects the
 * necessary [TransactionControl].
 */
internal class ExposedRepositoryFactory(
    private val control: TransactionControl,
    private val registry: RepositoryRegistry
) : RepositoryFactory {
    private val repositories = mutableMapOf<KClass<*>, AnyRepository>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Repository<*, *, *>> get(type: KClass<T>): T =
        repositories.getOrPut(type) {
            val implementation =
                this.registry.getOrElse(type) {
                    error("No binding for repository $type exists")
                }

            return@getOrPut implementation(this.control)
        } as T
}
