plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            // Serialization (TypeConverters for Room)
            implementation(libs.kotlinx.serialization.json)
            // Room KMP
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            // Koin
            implementation(libs.koin.core)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
