package io.github.briangits.persistence.conventions.publishing

import org.gradle.api.provider.Property

interface ArtifactExtension {
    val name: Property<String>
    val description: Property<String>
}
