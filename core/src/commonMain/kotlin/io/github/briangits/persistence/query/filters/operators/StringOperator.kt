package io.github.briangits.persistence.query.filters.operators

import kotlin.reflect.KProperty1

// String operators
sealed interface StringOperator<T : Any> : ValueOperator<T, String>

data class Like<T : Any>(
    override val prop: KProperty1<T, String>,
    override val value: String
) : StringOperator<T>

data class Contains<T : Any>(
    override val prop: KProperty1<T, String>,
    override val value: String
) : StringOperator<T>

data class StartsWith<T : Any>(
    override val prop: KProperty1<T, String>,
    override val value: String
) : StringOperator<T>

data class EndsWith<T : Any>(
    override val prop: KProperty1<T, String>,
    override val value: String
) : StringOperator<T>

data class Matches<T : Any>(
    override val prop: KProperty1<T, String>,
    override val value: String
) : StringOperator<T>
