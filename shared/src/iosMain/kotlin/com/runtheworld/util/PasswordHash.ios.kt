package com.runtheworld.util

import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.CommonCrypto.CC_SHA256
import platform.CommonCrypto.CC_SHA256_DIGEST_LENGTH
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.UByteVar

@OptIn(ExperimentalForeignApi::class)
actual fun sha256(input: String): String {
    val data = (input as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return input
    return memScoped {
        val digest = allocArray<UByteVar>(CC_SHA256_DIGEST_LENGTH)
        CC_SHA256(data.bytes, data.length.toUInt(), digest)
        digest.readBytes(CC_SHA256_DIGEST_LENGTH)
            .joinToString("") { "%02x".format(it) }
    }
}
