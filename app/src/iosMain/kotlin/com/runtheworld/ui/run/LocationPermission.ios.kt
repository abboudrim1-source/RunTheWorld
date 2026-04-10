package com.runtheworld.ui.run

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue

@Composable
actual fun LocationPermissionEffect(
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    // On iOS, CLLocationManager triggers the system dialog automatically when
    // locationUpdates() is first collected. We just forward to onGranted here
    // so the ViewModel starts listening; the OS dialog appears at that moment.
    val stableGranted by rememberUpdatedState(onGranted)
    LaunchedEffect(Unit) { stableGranted() }
}
