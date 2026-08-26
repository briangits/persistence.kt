plugins {
    alias(kt.plugins.jvm)
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
}
