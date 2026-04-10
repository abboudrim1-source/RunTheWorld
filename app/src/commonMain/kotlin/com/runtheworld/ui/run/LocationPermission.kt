package com.runtheworld.ui.run

import androidx.compose.runtime.Composable

/**
 * Side-effect composable: on first composition it checks / requests location
 * permission and calls [onGranted] or [onDenied] exactly once.
 *
 * Android: uses ActivityResultContracts.RequestMultiplePermissions.
 * iOS: CLLocationManager requests the permission automatically on first use,
 *      so we always forward to onGranted and let the OS show the dialog.
 */
@Composable
expect fun LocationPermissionEffect(
    onGranted: () -> Unit,
    onDenied: () -> Unit
)
