package io.github.briangits.persistence.exposed.filters

import io.github.briangits.persistence.exposed.relations.PropertyColumRelation
import io.github.briangits.persistence.exposed.relations.PropertyColumRelations
import io.github.briangits.persistence.query.Filters
import io.github.briangits.persistence.query.filters.operators.AllOf
import io.github.briangits.persistence.query.filters.operators.ArrayOperator
import io.github.briangits.persistence.query.filters.operators.Between
import io.github.briangits.persistence.query.filters.operators.Contains
import io.github.briangits.persistence.query.filters.operators.EndsWith
import io.github.briangits.persistence.query.filters.operators.Eq
import io.github.briangits.persistence.query.filters.operators.EqualityOperator
import io.github.briangits.persistence.query.filters.operators.FieldOperator
import io.github.briangits.persistence.query.filters.operators.GroupOperator
import io.github.briangits.persistence.query.filters.operators.Gt
import io.github.briangits.persistence.query.filters.operators.Gte
import io.github.briangits.persistence.query.filters.operators.In
import io.github.briangits.persistence.query.filters.operators.IsNotNull
import io.github.briangits.persistence.query.filters.operators.IsNull
import io.github.briangits.persistence.query.filters.operators.Like
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

typealias AnyColumn = Column<Any>
typealias StringColumn = Column<String?>

private fun <T : Any, TOperator : EqualityOperator<T, *>> TOperator.compile(
    colum: AnyColumn,
    op: AnyColumn.(Any) -> Op<Boolean>
): Op<Boolean> = colum.op(this.value as Any)

@Suppress("UNCHECKED_CAST")
private fun <T : Any, TOperator : ValueComparisonOperator<T, *>> TOperator.compile(
    colum: AnyColumn,
    op: AnyColumn.(Comparable<Any>) -> Op<Boolean>
): Op<Boolean> = colum.op(this.value as Comparable<Any>)

private fun <T : Any, TOperator : Between<T, *>> TOperator.compile(
    colum: AnyColumn,
    op: AnyColumn.(Any, Any) -> Op<Boolean>
): Op<Boolean> = colum.op(this.start as Any, this.end as Any)

@Suppress("UNCHECKED_CAST")
private fun <T : Any, TOperator : StringOperator<T>> TOperator.compile(
    colum: AnyColumn,
    op: StringColumn.(String) -> Op<Boolean>
): Op<Boolean> = (colum as StringColumn).op(this.value)

@Suppress("UNCHECKED_CAST")
private fun <T : Any, TOperator : ArrayOperator<T, *>> TOperator.compile(
    colum: AnyColumn,
    op: AnyColumn.(Iterable<Any>) -> Op<Boolean>
): Op<Boolean> = colum.op(this.values as Iterable<Any>)

private fun <T : Any, TOperator : NullOperator<T, *>> TOperator.compile(
    colum: AnyColumn,
    op: AnyColumn.(TOperator) -> Op<Boolean>
): Op<Boolean> = colum.op(this)

private fun <T : Any, V> FieldOperator<T, V>.compile(column: AnyColumn) =
    when (this) {
        is Eq<T, *> -> compile(column) { eq(it) }
        is NEq<T, *> -> compile(column) { neq(it) }

        is Gt<T, *> -> compile(column) { greater(it) }
        is Gte<T, *> -> compile(column) { greaterEq(it) }
        is Lt<T, *> -> compile(column) { less(it) }
        is Lte<T, *> -> compile(column) { lessEq(it) }
        is Between<T, *> -> compile(column) { start, end -> between(start, end) }

        is Like<T> -> compile(column) { like(it) }
        is Contains<T> -> compile(column) {
            val pattern = LikePattern.ofLiteral("")
                .plus("%")
                .plus(LikePattern.ofLiteral(it))
                .plus("%")

            like(pattern)
        }
        is StartsWith<T> -> compile(column) { like(LikePattern.ofLiteral(it) + "%") }
        is EndsWith<T> -> compile(column) {
            val pattern = LikePattern.ofLiteral("")
                .plus("%")
                .plus(LikePattern.ofLiteral(it))

            like(pattern)
        }
        is Matches<T> -> compile(column) { regexp(it) }

        is In<T, *> -> compile(column) { inList(it) }
        is NotIn<T, *> -> compile(column) { notInList(it) }

        is IsNull<T, *> -> compile(column) { isNull() }
        is IsNotNull<T, *> -> compile(column) { isNotNull() }
    }

private fun <T : Any> Operator.compile(relations: PropertyColumRelations<T>): Op<Boolean> =
    when (this) {
        is GroupOperator<*> -> {
            operators.map { it.compile(relations) }.let {
                when (this) {
                    is AllOf<*> -> it.fold(Op.TRUE as Op<Boolean>) { a, b -> a and b }
                    is OneOf<*> -> it.fold(Op.FALSE as Op<Boolean>) { a, b -> a or b }
                    is Not<*> -> not(it.fold(Op.TRUE as Op<Boolean>) { a, b -> a and b })
                }
            }
        }

        is FieldOperator<*, *> -> {
            val relation =
                relations.firstOrNull { it.prop == prop }
                    ?: error("No relation found for property $prop")

            @Suppress("UNCHECKED_CAST")
            val column =
                when (relation) {
                    is PropertyColumRelation.Id -> relation.column
                    is PropertyColumRelation.Field -> relation.column
                } as AnyColumn

            this.compile(column)
        }
    }

infix fun <T : Any, TFilters : Filters<T, TFilters>> TFilters.compile(
    relations: PropertyColumRelations<T>
): Op<Boolean> = this.build().compile(relations)
