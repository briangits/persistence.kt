package io.github.briangits.persistence.exposed

import io.github.briangits.persistence.exposed.repository.AnyRepository
import io.github.briangits.persistence.exposed.repository.RepositoryImplementation
import io.github.briangits.persistence.repository.Repository
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.v1.jdbc.Database
import kotlin.reflect.KClass

interface PersistenceBuilder {
    fun <T : Repository<*, *, *>> bind(
        type: KClass<T>,
        implementation: RepositoryImplementation<T>
    )
}

inline fun <reified T : Repository<*, *, *>> PersistenceBuilder.bind(
    noinline implementation: RepositoryImplementation<T>
) = this.bind(T::class, implementation)

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

    fun build(): ExposedPersistence = ExposedPersistence(database, registry, dispatcher)
}
