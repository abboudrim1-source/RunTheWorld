package com.runtheworld.data.network

object NetworkConfig {
    // 10.0.2.2 = host machine from Android emulator
    // 192.168.0.101 = host machine from a real device on the same WiFi
    val BASE_URL: String get() = if (isEmulator()) "http://10.0.2.2:8080" else "http://192.168.0.102:8080"
}

expect fun isEmulator(): Boolean
