package com.runtheworld.platform

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.runtheworld.domain.model.GpsPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidLocationService(private val context: Context) : LocationService {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun locationUpdates(): Flow<GpsPoint> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2_500L)
            .setMinUpdateIntervalMillis(2_000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(
                        GpsPoint(
                            lat = loc.latitude,
                            lng = loc.longitude,
                            timestamp = loc.time
                        )
                    )
                }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, context.mainLooper)
        awaitClose { fusedClient.removeLocationUpdates(callback) }
    }

    override suspend fun requestPermission(): Boolean {
        // Permission must be requested from an Activity/Fragment before calling locationUpdates().
        // This method exists for API symmetry; on Android use ActivityResultContracts at the UI layer.
        return hasLocationPermission()
    }

    private fun hasLocationPermission(): Boolean {
        val fineGranted = android.content.pm.PackageManager.PERMISSION_GRANTED ==
                context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseGranted = android.content.pm.PackageManager.PERMISSION_GRANTED ==
                context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineGranted || coarseGranted
    }
}
