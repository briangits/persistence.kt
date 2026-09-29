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
            jvmTarget = JvmTarget.JVM_11
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
        commonTest.dependencies {
            implementation(kt.test)
        }
    }
}

library {
    name = "core"
    description = "Core peristence abstraction API for persistence.kt"
}
