package io.github.briangits.persistence.conventions.publishing

import org.gradle.api.provider.Property

interface LibraryExtension {
    val name: Property<String>
    val description: Property<String>
}
