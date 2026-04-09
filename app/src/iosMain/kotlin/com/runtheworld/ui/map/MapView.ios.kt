package com.runtheworld.ui.map

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.runtheworld.domain.model.GpsPoint
import com.runtheworld.domain.model.Territory
import com.runtheworld.ui.theme.parseHexColor
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.*
import platform.UIKit.UIColor

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun RunTheWorldMap(
    modifier: Modifier,
    territories: List<Territory>,
    currentUserUsername: String?,
    currentPath: List<GpsPoint>,
    userLocation: GpsPoint?
) {
    val mapView = remember { MKMapView() }

    // Re-draw overlays whenever territories or path change
    LaunchedEffect(territories, currentPath, userLocation) {
        mapView.removeOverlays(mapView.overlays)

        // Territory polygons
        territories.forEach { territory ->
            val coords = territory.polygon.map {
                CLLocationCoordinate2DMake(it.lat, it.lng)
            }
            // MKPolygon needs a C array — use kotlinx.cinterop
            // For simplicity we store color info in the subtitle of the overlay via a custom subclass approach.
            // This is a scaffold; a production implementation would subclass MKPolygonRenderer.
            val polygon = MKPolygon.polygonWithCoordinates(
                coords = kotlinx.cinterop.nativeHeap.let { _ ->
                    // Allocate array for CLLocationCoordinate2D structs
                    @Suppress("UNCHECKED_CAST")
                    null as kotlinx.cinterop.CPointer<platform.CoreLocation.CLLocationCoordinate2D>? // TODO: fill via cinterop
                },
                count = coords.size.toULong()
            ) ?: return@forEach
            mapView.addOverlay(polygon)
        }

        // Current path polyline
        if (currentPath.size > 1) {
            val polyline = MKPolyline.polylineWithCoordinates(
                coordinates = null, // TODO: fill via cinterop
                count = currentPath.size.toULong()
            ) ?: return@LaunchedEffect
            mapView.addOverlay(polyline)
        }

        // Pan to user location
        userLocation?.let { gps ->
            val region = MKCoordinateRegionMakeWithDistance(
                centerCoordinate = CLLocationCoordinate2DMake(gps.lat, gps.lng),
                latitudinalMeters = 1500.0,
                longitudinalMeters = 1500.0
            )
            mapView.setRegion(region, animated = true)
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            mapView.showsUserLocation = true
            mapView.delegate = object : NSObject(), MKMapViewDelegateProtocol {
                override fun mapView(mapView: MKMapView, rendererForOverlay: MKOverlayProtocol): MKOverlayRenderer {
                    return when (rendererForOverlay) {
                        is MKPolygon -> MKPolygonRenderer(rendererForOverlay).also { r ->
                            r.fillColor = UIColor.blueColor.colorWithAlphaComponent(0.25)
                            r.strokeColor = UIColor.blueColor.colorWithAlphaComponent(0.8)
                            r.lineWidth = 2.0
                        }
                        is MKPolyline -> MKPolylineRenderer(rendererForOverlay).also { r ->
                            r.strokeColor = UIColor(red = 0.2, green = 0.69, blue = 1.0, alpha = 1.0)
                            r.lineWidth = 5.0
                        }
                        else -> MKOverlayRenderer(rendererForOverlay)
                    }
                }
            }
            mapView
        },
        update = { /* LaunchedEffect handles updates */ }
    )
}
