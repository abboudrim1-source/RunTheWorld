plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    jvmToolchain(17)
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            // Serialization (TypeConverters for Room & Ktor JSON)
            implementation(libs.kotlinx.serialization.json)
            // Room KMP
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            // Koin
            implementation(libs.koin.core)
            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            // Multiplatform Settings (user profile storage)
            implementation(libs.multiplatform.settings)
            // Lifecycle ViewModel
            implementation(libs.lifecycle.viewmodel)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android)
            // GPS / Fused Location Provider
            implementation(libs.play.services.location)
            // Ktor Android Engine
            implementation(libs.ktor.client.okhttp)
        }
    }
}

android {
    namespace = "com.runtheworld.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Room schema output directory (version-controlled)
room {
    schemaDirectory("$projectDir/schemas")
}

// KSP for Room annotation processing (Android only)
dependencies {
    add("kspAndroid", libs.room.compiler)
}
