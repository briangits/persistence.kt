package io.github.briangits.persistence.repository

import kotlin.reflect.KClass

/**
 * Service interface for resolving and retrieving [Repository] instances.
 * 
 * Typically implemented by the transactional context or a dependency injection 
 * container to provide scoped repository access.
 */
interface RepositoryFactory {
    /**
     * Resolves and returns a repository instance of the requested [type].
     *
     * @param T The specific repository implementation type.
     * @param type The [KClass] of the repository to resolve.
     * @return An initialized instance of the requested repository.
     */
    fun <T : Repository<*, *, *>> get(type: KClass<T>): T
}

/**
 * Resolves and returns a repository instance using the reified type parameter [T].
 * 
 * @param T The specific repository implementation type.
 * @return An initialized instance of the requested repository.
 */
inline fun <reified T : Repository<*, *, *>> RepositoryFactory.get(): T = get(T::class)

