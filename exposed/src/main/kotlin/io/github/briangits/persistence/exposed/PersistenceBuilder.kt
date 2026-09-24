package io.github.briangits.persistence.exposed

import io.github.briangits.persistence.exposed.repository.AnyRepository
import io.github.briangits.persistence.exposed.repository.RepositoryImplementation
import io.github.briangits.persistence.repository.Repository
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.v1.jdbc.Database
import kotlin.reflect.KClass

/**
 * Builder interface for configuring the [ExposedPersistence] instance.
 *
 * Provides methods for binding repository implementations to their respective interface types
 * within the persistence context.
 */
interface PersistenceBuilder {
    /**
     * Binds a specific repository implementation to an interface type.
     *
     * @param T The repository interface type.
     * @param type The [KClass] of the repository interface.
     * @param implementation The factory function to create the repository implementation.
     */
    fun <T : Repository<*, *, *>> bind(
        type: KClass<T>,
        implementation: RepositoryImplementation<T>
    )
}

inline fun <reified T : Repository<*, *, *>> PersistenceBuilder.bind(
    noinline implementation: RepositoryImplementation<T>
) = bind(T::class, implementation)

/**
 * Internal implementation of [PersistenceBuilder].
 *
 * @param database The Exposed [Database] instance.
 * @param dispatcher The [CoroutineDispatcher] used for database operations.
 * @param block A configuration block for defining repository bindings.
 */
internal class PersistenceBuilderImpl(
    private val database: Database,
    private val dispatcher: CoroutineDispatcher,
    block: PersistenceBuilder.() -> Unit
) : PersistenceBuilder {
    private val registry = mutableMapOf<KClass<*>, RepositoryImplementation<AnyRepository>>()

    init {
        block()
    }

    override fun <T : Repository<*, *, *>> bind(
        type: KClass<T>,
        implementation: RepositoryImplementation<T>
    ) {
        registry[type] = implementation
    }

    /**
     * Builds and returns an [ExposedPersistence] instance based on the configured bindings.
     */
    fun build(): ExposedPersistence = ExposedPersistence(database, registry, dispatcher)
}
