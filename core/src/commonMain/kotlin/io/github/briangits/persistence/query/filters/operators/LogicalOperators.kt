package io.github.briangits.persistence.query.filters.operators

sealed interface LogicalOperator : Operator {

    val operators: List<Operator>

}

data class AllOf(
    override val operators: List<Operator>
) : LogicalOperator

data class OneOf(
    override val operators: List<Operator>
) : LogicalOperator

data class Not(
    override val operators: List<Operator>
) : LogicalOperator
