package com.mapvina.navigation.core.utils

import com.mapvina.geojson.model.Point
import com.mapvina.navigation.core.navigation.MapVinaNavigationOptions
import com.mapvina.navigation.core.routeprogress.RouteProgress
import com.mapvina.geojson.turf.TurfMeasurement
import com.mapvina.geojson.turf.TurfMisc
import com.mapvina.geojson.turf.TurfUnit
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
            val closestIntersectionFeature = TurfMisc.nearestPointOnLine(
                snappedPoint,
                intersections.map(StepIntersection::location)
            )

            val closestIntersection = closestIntersectionFeature.geometry as Point
            if (closestIntersection == snappedPoint) {
                return navigationOptions.offRouteThresholdRadiusMeters
            }

            val distanceToNextIntersection = TurfMeasurement.distance(
                snappedPoint,
                closestIntersection,
                TurfUnit.METERS
            )

            if (distanceToNextIntersection <= navigationOptions.maneuverZoneRadius) {
                return navigationOptions.offRouteThresholdRadiusMeters / 2
            }
        }

        return navigationOptions.offRouteThresholdRadiusMeters
    }
}