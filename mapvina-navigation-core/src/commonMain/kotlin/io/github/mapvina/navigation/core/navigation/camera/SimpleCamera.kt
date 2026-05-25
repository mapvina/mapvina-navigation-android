package io.github.mapvina.navigation.core.navigation.camera

import io.github.mapvina.spatialk.geojson.LineString
import io.github.mapvina.spatialk.geojson.Point
import io.github.mapvina.navigation.core.models.DirectionsRoute
import io.github.mapvina.navigation.core.utils.Constants
import io.github.mapvina.spatialk.polyline.PolylineEncoding
import io.github.mapvina.spatialk.turf.measurement.bearingTo
import io.github.mapvina.spatialk.units.Bearing
import io.github.mapvina.spatialk.units.DMS.Degrees
import io.github.mapvina.navigation.core.navigation.MapVinaNavigation

/**
 * The default camera used by [MapVinaNavigation].
 *
 * @since 0.10.0
 */
open class SimpleCamera : Camera {

    private var routeCoordinates: List<Point> = ArrayList()
    private var initialBearing = 0.0
    private var initialRoute: DirectionsRoute? = null

    override fun bearing(routeInformation: RouteInformation): Double {
        return routeInformation.route?.let { route ->
            setupLineStringAndBearing(route)
            initialBearing
        }
            ?: routeInformation.location?.bearing?.toDouble()
            ?: 0.0
    }

    override fun target(routeInformation: RouteInformation): Point? {
        return routeInformation.route?.let { route ->
            setupLineStringAndBearing(route)
            val firstPoint = routeCoordinates.first()
            Point(
                longitude = firstPoint.longitude,
                latitude = firstPoint.latitude,
                altitude = firstPoint.altitude
            )
        } ?: routeInformation.location?.let { location ->
            Point(
                longitude = location.longitude,
                latitude = location.latitude,
                altitude = location.altitude
            )
        }
    }

    override fun tilt(routeInformation: RouteInformation): Double {
        return DEFAULT_TILT.toDouble()
    }

    override fun zoom(routeInformation: RouteInformation): Double {
        return DEFAULT_ZOOM
    }

    override fun overview(routeInformation: RouteInformation): List<Point> {
        if (routeCoordinates.isEmpty()) {
            buildRouteCoordinatesFromRouteData(routeInformation)
        }

        return routeCoordinates
    }

    private fun buildRouteCoordinatesFromRouteData(routeInformation: RouteInformation) {
        routeInformation.route?.let { route ->
            setupLineStringAndBearing(route)
        } ?: routeInformation.routeProgress?.let { routeProgress ->
            setupLineStringAndBearing(routeProgress.directionsRoute)
        }
    }

    private fun setupLineStringAndBearing(route: DirectionsRoute) {
        if (initialRoute != null && route == initialRoute) {
            return // no need to recalculate these values
        }

        initialRoute = route
        routeCoordinates = generateRouteCoordinates(route)
        if (routeCoordinates.size >= 2) {
            val start = routeCoordinates.first()
            val end = routeCoordinates[1]
            initialBearing = (start.bearingTo(end) - Bearing.North).toDouble(Degrees)
        } else {
            initialBearing = 0.0
        }
    }

    private fun generateRouteCoordinates(route: DirectionsRoute?): List<Point> {
        return route?.let { rte ->
            val lineString = LineString(PolylineEncoding.decode(rte.geometry, Constants.PRECISION_6))
            lineString.coordinates.map { Point(it) }
        } ?: emptyList()
    }

    protected companion object {
        const val DEFAULT_TILT: Int = 50
        const val DEFAULT_ZOOM: Double = 15.0
    }
}
