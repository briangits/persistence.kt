package io.github.briangits.persistence.query.filters.operators

/**
 * Represents logical grouping operators used to combine multiple query constraints.
 *
 * @param T The entity class targeting these group filters.
 */
sealed interface GroupOperator<T : Any> : Operator {
    /**
     * The list of nested [Operator] instances grouped under this composite operator.
     */
    val operators: List<Operator>
}

/**
 * Evaluates to true only if all child query constraints in [operators] evaluate to true.
 *
 * @param T The entity class targeting these group filters.
 */
data class AllOf<T : Any>(
    override val operators: List<Operator>
) : GroupOperator<T>

/**
 * Evaluates to true if at least one child query constraint in [operators] evaluates to true.
 *
 * @param T The entity class targeting these group filters.
 */
data class OneOf<T : Any>(
    override val operators: List<Operator>
) : GroupOperator<T>

/**
 * Inverts the logical outcome of all grouped child constraints in [operators].
 *
 * @param T The entity class targeting these group filters.
 */
data class Not<T : Any>(
    override val operators: List<Operator>
) : GroupOperator<T>
