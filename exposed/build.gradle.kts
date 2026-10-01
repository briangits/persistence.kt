plugins {
    alias(kt.plugins.multiplatform)

    // Publishing
    id("io.github.briangits.persistence.conventions.publishing")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(projects.core)

            // Coroutines
            implementation(kotlinx.coroutines)
        }

        commonTest.dependencies {
            implementation(kt.test)
            implementation(kotlinx.coroutines.test)
        }

        jvmMain.dependencies {
            // Exposed
            api(exposed.core)
            api(exposed.jdbc)
        }

        jvmTest.dependencies {
            implementation(exposed.h2)
        }
    }
}

library {
    name = "exposed"
    description = "Persistence API implememntation with Exposed for persistence.kt"
}
