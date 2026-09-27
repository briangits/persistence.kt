package io.github.briangits.persistence.query.filters

import io.github.briangits.persistence.query.properties.Properties
import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.filters.operators.Operator

internal class FiltersImpl<T : Any, TSelf>(
    override val factory: () -> TSelf
) : Filters<T, TSelf>  where TSelf : Filters<T, TSelf>, TSelf : Properties<T> {

    override val operators: List<Operator>
    field = mutableListOf<Operator>()

    override fun add(operator: Operator) {
        operators += operator
    }

    override fun build(): AllOf {
        return AllOf(operators)
    }

}