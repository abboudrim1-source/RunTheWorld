package com.runtheworld.util

import kotlin.random.Random

/** Platform-specific SHA-256 hash of (password + salt). */
expect fun sha256(input: String): String

fun generateSalt(): String =
    Random.nextBytes(16).toHex()

fun hashPassword(password: String, salt: String): String =
    sha256(password + salt)

private fun ByteArray.toHex(): String =
    joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
