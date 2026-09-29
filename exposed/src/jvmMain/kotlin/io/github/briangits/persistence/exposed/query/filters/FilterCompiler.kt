package io.github.briangits.persistence.exposed.query.filters

import io.github.briangits.persistence.exposed.relations.PropertyColumnRelations
import io.github.briangits.persistence.query.filters.Filters
import io.github.briangits.persistence.query.filters.build
import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.filters.operators.ArrayOperator
import io.github.briangits.persistence.query.filters.operators.Between
import io.github.briangits.persistence.query.filters.operators.ComparisonOperator
import io.github.briangits.persistence.query.filters.operators.Contains
import io.github.briangits.persistence.query.filters.operators.EndsWith
import io.github.briangits.persistence.query.filters.operators.Eq
import io.github.briangits.persistence.query.filters.operators.EqualityOperator
import io.github.briangits.persistence.query.filters.operators.FieldOperator
import io.github.briangits.persistence.query.filters.operators.Gt
import io.github.briangits.persistence.query.filters.operators.Gte
import io.github.briangits.persistence.query.filters.operators.In
import io.github.briangits.persistence.query.filters.operators.IsNotNull
import io.github.briangits.persistence.query.filters.operators.IsNull
import io.github.briangits.persistence.query.filters.operators.LogicalOperator
import io.github.briangits.persistence.query.filters.operators.Lt
import io.github.briangits.persistence.query.filters.operators.Lte
import io.github.briangits.persistence.query.filters.operators.Matches
import io.github.briangits.persistence.query.filters.operators.NEq
import io.github.briangits.persistence.query.filters.operators.Not
import io.github.briangits.persistence.query.filters.operators.NotIn
import io.github.briangits.persistence.query.filters.operators.NullOperator
import io.github.briangits.persistence.query.filters.operators.OneOf
import io.github.briangits.persistence.query.filters.operators.Operator
import io.github.briangits.persistence.query.filters.operators.StartsWith
import io.github.briangits.persistence.query.filters.operators.StringOperator
import io.github.briangits.persistence.query.filters.operators.ValueComparisonOperator
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.LikePattern
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.between
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.not
import org.jetbrains.exposed.v1.core.notInList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.core.regexp

private fun StringOperator.compile(column: Column<String?>): Op<Boolean> =
    when (this) {
        is Contains -> column.like(
            LikePattern.ofLiteral("")
                .plus("%")
                .plus(LikePattern.ofLiteral(value))
                .plus("%")
        )
        is StartsWith -> column.like(LikePattern.ofLiteral(value) + "%")
        is EndsWith -> column.like(
            LikePattern.ofLiteral("")
                .plus("%")
                .plus(LikePattern.ofLiteral(value))
        )
        is Matches -> column.regexp(value)
    }

private fun <T> FieldOperator<T>.compile(column: Column<T>) =
    @Suppress("UNCHECKED_CAST")
    when (this) {
        is EqualityOperator -> when (this) {
            is Eq<*> -> column.eq(value)
            is NEq<*> -> column.neq(value)
        }

        is ComparisonOperator -> when (this) {
            is ValueComparisonOperator -> when (this) {
                is Gt<*> -> column.greater(value)
                is Gte<*> -> column.greaterEq(value)
                is Lt<*> -> column.less(value)
                is Lte<*> -> column.lessEq(value)
            }
            is Between<*> -> column.between(start, end)
        }

        is StringOperator -> compile(column as Column<String?>)

        is ArrayOperator -> when (this) {
            is In -> column.inList(values)
            is NotIn -> column.notInList(values)
        }

        is NullOperator -> when (this) {
            is IsNull -> column.isNull()
            is IsNotNull -> column.isNotNull()
        }
    }


private fun <T> FieldOperator<T>.compile(relations: PropertyColumnRelations): Op<Boolean> =
    compile(column = relations[path])

internal fun Operator.compile(relations: PropertyColumnRelations): Op<Boolean> =
    when (this) {
        is LogicalOperator -> {
            when (this) {
                is AllOf ->
                    operators.fold(Op.TRUE as Op<Boolean>) { a, b ->
                        a and b.compile(relations)
                    }
                is OneOf ->
                    operators.fold(Op.FALSE as Op<Boolean>) {
                        a, b -> a or b.compile(relations)
                    }
                is Not -> not(operator.compile(relations))
            }
        }

        is FieldOperator<*> -> compile(relations)
    }

fun <T : Any, TFilters : Filters<T, TFilters>> TFilters.compile(
    relations: PropertyColumnRelations,
    factory: () -> TFilters
): Op<Boolean> =
    this.build(factory).compile(relations)
