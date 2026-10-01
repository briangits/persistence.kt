package io.github.briangits.persistence.query.filters

import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.filters.operators.Not
import io.github.briangits.persistence.query.filters.operators.OneOf
import io.github.briangits.persistence.query.filters.operators.Operator

private sealed interface FilterEntry<TSelf : Filters<TSelf>> {

    fun compile(factory: () -> TSelf): Operator

    class Value<TSelf : Filters<TSelf>>(
        val operator: Operator
    ) : FilterEntry<TSelf> {
        override fun compile(factory: () -> TSelf): Operator = operator
    }

    class All<TSelf : Filters<TSelf>>(
        val block: TSelf.() -> Unit
    ) : FilterEntry<TSelf> {
        override fun compile(factory: () -> TSelf): Operator =
            AllOf(
                operators = factory().run {
                    block()

                    compile(factory)
                }
            )
    }

    class One<TSelf : Filters<TSelf>>(
        val block: TSelf.() -> Unit
    ) : FilterEntry<TSelf> {
        override fun compile(factory: () -> TSelf): Operator =
            OneOf(
                operators = factory().run {
                    block()

                    compile(factory)
                }
            )
    }

    class Negated<TSelf : Filters<TSelf>>(
        val block: TSelf.() -> Unit
    ) : FilterEntry<TSelf> {
        override fun compile(factory: () -> TSelf): Operator =
            Not(
                operator = All(block).compile(factory)
            )
    }

}

internal class FiltersImpl<TSelf : Filters<TSelf>> : Filters<TSelf> {

    @Suppress("UNCHECKED_CAST")
    private val entries = mutableListOf<FilterEntry<TSelf>>()

    override fun add(operator: Operator) {
        entries += FilterEntry.Value(operator)
    }

    override fun allOf(block: TSelf.() -> Unit) {
        entries += FilterEntry.All(block)
    }

    override fun oneOf(block: TSelf.() -> Unit) {
        entries += FilterEntry.One(block)
    }

    override fun not(block: TSelf.() -> Unit) {
        entries += FilterEntry.Negated(block)
    }

    override fun compile(
        factory: () -> TSelf
    ): List<Operator> =
        entries.map { it.compile(factory) }

}
