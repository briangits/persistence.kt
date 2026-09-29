import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // KMP
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)

    // Publishing
    id("io.github.briangits.persistence.conventions.publishing")
}

kotlin {
    jvm()

    android {
        namespace = group.toString()
        compileSdk = 37
        minSdk = 21

        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    iosArm64()
    iosSimulatorArm64()

    macosArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
        }
    }
}

library {
    name = "uow"
    description = "Unit of Work API for persistence.kt"
}
