package com.runtheworld.data.network

import android.os.Build

actual fun isEmulator(): Boolean =
    Build.FINGERPRINT.startsWith("generic") ||
    Build.FINGERPRINT.startsWith("unknown") ||
    Build.MODEL.contains("Emulator") ||
    Build.MODEL.contains("Android SDK") ||
    Build.MANUFACTURER.contains("Genymotion") ||
    Build.HARDWARE.contains("goldfish") ||
    Build.HARDWARE.contains("ranchu") ||
    Build.PRODUCT.contains("sdk") ||
    Build.PRODUCT.contains("emulator")
