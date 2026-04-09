package com.runtheworld.presentation.run

import platform.Foundation.NSDate

actual fun currentTimeMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000).toLong()
