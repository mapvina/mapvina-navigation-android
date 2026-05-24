package com.mapvina.navigation.core.utils

import com.mapvina.spatialk.geojson.Point
import com.mapvina.navigation.core.navigation.MapVinaNavigationOptions
import com.mapvina.navigation.core.routeprogress.RouteProgress
import com.mapvina.spatialk.turf.measurement.distance
import com.mapvina.spatialk.turf.misc.nearestPointTo
import com.mapvina.spatialk.units.International.Meters
import com.mapvina.navigation.core.models.StepIntersection
import kotlin.jvm.JvmStatic

object ToleranceUtils {

    /**
     * Reduce the offRouteMinimumDistanceMetersBeforeWrongDirection if we are close to an intersection.
     * You can define these values in the navigationOptions
     */
    @JvmStatic
    fun dynamicOffRouteRadiusTolerance(
        snappedPoint: Point,
        routeProgress: RouteProgress,
        navigationOptions: MapVinaNavigationOptions
    ): Double {
        val intersections = routeProgress.currentLegProgress.currentStepProgress.intersections
        if (intersections != null && intersections.size >= 2) {
            val closestIntersectionFeature = intersections.map(StepIntersection::location)
                .nearestPointTo(snappedPoint)

            val closestIntersection = closestIntersectionFeature.geometry
            if (closestIntersection == snappedPoint) {
                return navigationOptions.offRouteThresholdRadiusMeters
            }

            val distanceToNextIntersection = distance(
                snappedPoint.coordinates,
                closestIntersection.coordinates
            ).toDouble(Meters)

            if (distanceToNextIntersection <= navigationOptions.maneuverZoneRadius) {
                return navigationOptions.offRouteThresholdRadiusMeters / 2
            }
        }

        return navigationOptions.offRouteThresholdRadiusMeters
    }
}