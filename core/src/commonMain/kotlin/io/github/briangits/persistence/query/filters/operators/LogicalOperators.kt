package io.github.briangits.persistence.query.filters.operators

sealed interface LogicalOperator : Operator

data class AllOf(
    val operators: List<Operator>
) : LogicalOperator

data class OneOf(
    val operators: List<Operator>
) : LogicalOperator

data class Not(
    val operator: Operator
) : LogicalOperator
