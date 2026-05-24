package com.mapvina.navigation.core.utils

import com.mapvina.spatialk.geojson.LineString
import com.mapvina.spatialk.geojson.Point
import com.mapvina.navigation.core.models.LegStep
import com.mapvina.spatialk.polyline.PolylineEncoding
import com.mapvina.spatialk.turf.measurement.distance
import com.mapvina.spatialk.turf.misc.nearestPointTo
import com.mapvina.spatialk.units.International.Meters
import kotlin.jvm.JvmStatic

object MeasurementUtils {

    /**
     * Calculates the distance between the users current raw [android.location.Location] object
     * to the closest [Point] in the [LegStep].
     *
     * @param usersRawLocation [Point] the raw location where the user is currently located
     * @param step             [LegStep] to calculate the closest point on the step to our
     * predicted location
     * @return double in distance meters
     * @since 0.2.0
     */
    @JvmStatic
    fun userTrueDistanceFromStep(usersRawLocation: Point, step: LegStep): Double {
        // Check that the leg step contains geometry.
        if (step.geometry.isEmpty()) {
            return 0.0
        }

        // Get the lineString from the step geometry.
        val lineString = LineString(PolylineEncoding.decode(step.geometry, Constants.PRECISION_6))

        // Make sure that the step coordinates isn't less than size 2. If the points equal each other,
        // the distance is obviously zero, so return 0 to avoid executing additional unnecessary code.
        if (lineString.coordinates.isEmpty() || usersRawLocation.coordinates == lineString.coordinates
                .first()
        ) {
            return 0.0
        }

        if (lineString.coordinates.size == 1) {
            return distance(
                usersRawLocation.coordinates,
                lineString.coordinates.first()
            ).toDouble(Meters)
        }

        val snappedPointFeature = lineString.nearestPointTo(usersRawLocation.coordinates)
        val snappedPoint = snappedPointFeature.geometry
        if (snappedPoint.latitude.isInfinite() || snappedPoint.longitude.isInfinite()) {
            return distance(
                usersRawLocation.coordinates,
                lineString.coordinates.first()
            ).toDouble(Meters)
        }

        val dist = distance(usersRawLocation.coordinates, snappedPoint.coordinates).toDouble(Meters)
        return if (!dist.isNaN()) dist else 0.0
    }
}

