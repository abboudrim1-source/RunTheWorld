plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.runtheworld"
version = "1.0.0"

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages)
    
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite.jdbc)
    
    implementation(libs.logback)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.create("stage") {
    dependsOn("installDist")
}
