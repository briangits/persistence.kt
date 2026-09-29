package io.github.briangits.persistence.query.filters

import io.github.briangits.persistence.query.BaseFilters
import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.filters.operators.Not
import io.github.briangits.persistence.query.filters.operators.OneOf
import io.github.briangits.persistence.query.filters.operators.Operator
import opensavvy.pedestal.weak.ExperimentalWeakApi
import opensavvy.pedestal.weak.WeakMap
import opensavvy.pedestal.weak.getOrPut

private sealed interface FilterEntry<TSelf : Filters<*, TSelf>> {

    fun compile(factory: () -> TSelf): Operator

    class Value<TSelf : Filters<*, TSelf>>(
        val operator: Operator
    ) : FilterEntry<TSelf> {
        override fun compile(factory: () -> TSelf): Operator = operator
    }

    class All<TSelf : Filters<*, TSelf>>(
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

    class One<TSelf : Filters<*, TSelf>>(
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

    class Negated<TSelf : Filters<*, TSelf>>(
        val block: TSelf.() -> Unit
    ) : FilterEntry<TSelf> {
        override fun compile(factory: () -> TSelf): Operator =
            Not(
                operator = All(block).compile(factory)
            )
    }

}

@OptIn(ExperimentalWeakApi::class)
interface Filters<T : Any, TSelf : Filters<T, TSelf>> : BaseFilters<T, TSelf> {

        companion object {
            private val state = WeakMap<Any, MutableList<FilterEntry<*>>>()
        }

    @Suppress("UNCHECKED_CAST")
    private val entries: MutableList<FilterEntry<TSelf>>
        get() = state.getOrPut(this) { mutableListOf() } as MutableList<FilterEntry<TSelf>>


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

fun <T : Any, TSelf : Filters<T, TSelf>> Filters<T, TSelf>.build(factory: () -> TSelf): AllOf =
    AllOf(operators = compile(factory))
