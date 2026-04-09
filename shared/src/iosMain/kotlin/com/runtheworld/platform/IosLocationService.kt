package com.runtheworld.platform

import com.runtheworld.domain.model.GpsPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.*
import platform.darwin.NSObject

class IosLocationService : LocationService {

    private val locationManager = CLLocationManager()

    override fun locationUpdates(): Flow<GpsPoint> = callbackFlow {
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val loc = didUpdateLocations.lastOrNull() as? CLLocation ?: return
                trySend(
                    GpsPoint(
                        lat = loc.coordinate.useContents { latitude },
                        lng = loc.coordinate.useContents { longitude },
                        timestamp = (loc.timestamp.timeIntervalSince1970 * 1000).toLong()
                    )
                )
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: platform.Foundation.NSError) {
                // Non-fatal — we simply skip the failed update
            }
        }

        locationManager.delegate = delegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = 5.0  // metres
        locationManager.startUpdatingLocation()

        awaitClose {
            locationManager.stopUpdatingLocation()
            locationManager.delegate = null
        }
    }

    override suspend fun requestPermission(): Boolean {
        locationManager.requestWhenInUseAuthorization()
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
               status == kCLAuthorizationStatusAuthorizedAlways
    }
}
