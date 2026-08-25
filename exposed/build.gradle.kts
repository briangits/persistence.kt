plugins {
    alias(kt.plugins.jvm)
}

dependencies {
    api(projects.core)

    implementation(exposed.core)
}
