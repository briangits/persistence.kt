plugins {
    alias(kt.plugins.jvm)

    // Publishing
    id("io.github.briangits.persistence.conventions.publishing")
}

dependencies {
    api(projects.core)

    // Exposed
    implementation(exposed.core)
    implementation(exposed.jdbc)

    // Coroutines
    implementation(kotlinx.coroutines)

    // Tests
    testImplementation(kt.test)
    testImplementation(exposed.h2)
    testImplementation(kotlinx.coroutines.test)
}

library {
    name = "exposed"
    description = "Persistence abstraction implememntation using Exposed"
}
