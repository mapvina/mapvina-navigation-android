package com.mapvina.navigation.core.navigation

import com.mapvina.navigation.core.location.engine.AppleLocationEngine
import com.mapvina.navigation.core.location.engine.LocationEngine
import com.mapvina.navigation.core.navigation.MapVinaNavigationOptions.Defaults
import com.mapvina.navigation.core.navigation.MapVinaNavigationOptions.RoundingIncrement
import com.mapvina.navigation.core.navigation.MapVinaNavigationOptions.TimeFormat
import com.mapvina.navigation.core.navigation.camera.Camera
import com.mapvina.navigation.core.navigation.camera.SimpleCamera
import com.mapvina.navigation.core.offroute.OffRoute
import com.mapvina.navigation.core.offroute.OffRouteDetector
import com.mapvina.navigation.core.route.FasterRoute
import com.mapvina.navigation.core.route.FasterRouteDetector
import com.mapvina.navigation.core.snap.Snap
import com.mapvina.navigation.core.snap.SnapToRoute
import com.mapvina.navigation.core.utils.RouteUtils

/**
 * A iOS platform specific wrapper for [MapVinaNavigation].
 *
 * You can also use [MapVinaNavigation] directly, but this leads to more configuration.
 *
 * Currently the only difference is, that the location engine is set to the [AppleLocationEngine]
 * by default.
 */
class IOSMapVinaNavigation(
    options: MapVinaNavigationOptions = MapVinaNavigationOptions(),
    locationEngine: LocationEngine = AppleLocationEngine(),
    cameraEngine: Camera = SimpleCamera(),
    snapEngine: Snap = SnapToRoute(),
    offRouteEngine: OffRoute = OffRouteDetector(),
    fasterRouteEngine: FasterRoute = FasterRouteDetector(options),
    routeUtils: RouteUtils = RouteUtils(),
) : MapVinaNavigation(
    options = options,
    locationEngine = locationEngine,
    cameraEngine = cameraEngine,
    snapEngine = snapEngine,
    offRouteEngine = offRouteEngine,
    fasterRouteEngine = fasterRouteEngine,
    routeUtils = routeUtils
) {

    fun toBuilder(): Builder {
        return Builder()
            .withOptions(options)
            .withLocationEngine(locationEngine)
            .withCameraEngine(cameraEngine)
            .withSnapEngine(snapEngine)
            .withOffRouteEngine(offRouteEngine)
            .withFasterRouteEngine(fasterRouteEngine)
            .withRouteUtils(routeUtils)
    }

    class Builder {
        private var options: MapVinaNavigationOptions = MapVinaNavigationOptions()
        private var locationEngine: LocationEngine = AppleLocationEngine()
        private var cameraEngine: Camera = SimpleCamera()
        private var snapEngine: Snap = SnapToRoute()
        private var offRouteEngine: OffRoute = OffRouteDetector()
        private var fasterRouteEngine: FasterRoute = FasterRouteDetector(options)
        private var routeUtils: RouteUtils = RouteUtils()

        fun withOptions(options: MapVinaNavigationOptions) = apply { this.options = options }
        fun withLocationEngine(locationEngine: LocationEngine) =
            apply { this.locationEngine = locationEngine }

        fun withCameraEngine(cameraEngine: Camera) = apply { this.cameraEngine = cameraEngine }
        fun withSnapEngine(snapEngine: Snap) = apply { this.snapEngine = snapEngine }
        fun withOffRouteEngine(offRouteEngine: OffRoute) =
            apply { this.offRouteEngine = offRouteEngine }

        fun withFasterRouteEngine(fasterRouteEngine: FasterRoute) =
            apply { this.fasterRouteEngine = fasterRouteEngine }

        fun withRouteUtils(routeUtils: RouteUtils) = apply { this.routeUtils = routeUtils }

        fun build(): IOSMapVinaNavigation {
            return IOSMapVinaNavigation(
                options = options,
                locationEngine = locationEngine,
                cameraEngine = cameraEngine,
                snapEngine = snapEngine,
                offRouteEngine = offRouteEngine,
                fasterRouteEngine = fasterRouteEngine,
                routeUtils = routeUtils
            )
        }
    }
}
