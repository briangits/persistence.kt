package io.github.briangits.persistence.query.filters.operators

import io.github.briangits.persistence.query.properties.PropertyPath

sealed interface StringOperator : ValueOperator<String>

data class Contains(
    override val path: PropertyPath<String?>,
    override val value: String
) : StringOperator

data class StartsWith(
    override val path: PropertyPath<String?>,
    override val value: String
) : StringOperator

data class EndsWith(
    override val path: PropertyPath<String?>,
    override val value: String
) : StringOperator

data class Matches(
    override val path: PropertyPath<String?>,
    override val value: String
) : StringOperator
