package com.mapvina.navigation.core

import com.mapvina.spatialk.geojson.LineString
import com.mapvina.spatialk.geojson.Point
import com.mapvina.navigation.core.location.Location
import com.mapvina.navigation.core.routeprogress.RouteProgress
import com.mapvina.navigation.core.utils.Constants
import com.mapvina.spatialk.turf.TurfMeasurement
import com.mapvina.spatialk.turf.TurfUnit

internal class MockLocationBuilder {
    fun buildDefaultMockLocationUpdate(lng: Double, lat: Double): Location {
        return buildMockLocationUpdate(lng, lat, 30f, 10f, System.currentTimeMillis())
    }

    fun buildPointAwayFromLocation(location: Location, distanceAway: Double): Point {
        val fromLocation = Point(
            location.longitude, location.latitude, location.altitude
        )
        return TurfMeasurement.destination(
            fromLocation,
            distanceAway,
            90.0,
            TurfUnit.METERS
        )
    }

    fun buildPointAwayFromPoint(point: Point, distanceAway: Double, bearing: Double): Point {
        return TurfMeasurement.destination(point, distanceAway, bearing, TurfUnit.METERS)
    }

    fun createCoordinatesFromCurrentStep(progress: RouteProgress): List<Point> {
        val currentStep = progress.currentLegProgress.currentStep
        val lineString = LineString(currentStep.geometry, Constants.PRECISION_6)
        return lineString.coordinates
    }

    private fun buildMockLocationUpdate(
        lngValue: Double,
        latValue: Double,
        speedValue: Float,
        horizontalAccuracyValue: Float,
        timeValue: Long
    ): Location {
        return Location(
            provider = "test",
            latitude = latValue,
            longitude = lngValue,
            speedMetersPerSeconds = speedValue,
            accuracyMeters = horizontalAccuracyValue,
            timeMilliseconds = timeValue
        )
    }
}
