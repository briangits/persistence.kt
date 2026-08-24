package io.github.briangits.persistence.query.filters.operators

sealed interface GroupOperator<T : Any> : Operator {
    val operators: List<Operator>
}

data class AllOf<T : Any>(
    override val operators: List<Operator>
) : GroupOperator<T>

data class OneOf<T : Any>(
    override val operators: List<Operator>
) : GroupOperator<T>

data class Not<T : Any>(
    override val operators: List<Operator>
) : GroupOperator<T>
