package io.github.mapvina.navigation.core.navigation

import android.content.Context
import io.github.mapvina.navigation.core.location.engine.LocationEngine
import io.github.mapvina.navigation.core.location.engine.LocationEngineProvider
import io.github.mapvina.navigation.core.navigation.camera.Camera
import io.github.mapvina.navigation.core.navigation.camera.SimpleCamera
import io.github.mapvina.navigation.core.offroute.OffRoute
import io.github.mapvina.navigation.core.offroute.OffRouteDetector
import io.github.mapvina.navigation.core.route.FasterRoute
import io.github.mapvina.navigation.core.route.FasterRouteDetector
import io.github.mapvina.navigation.core.snap.Snap
import io.github.mapvina.navigation.core.snap.SnapToRoute
import io.github.mapvina.navigation.core.utils.RouteUtils

/**
 * A Android platform specific wrapper for [MapVinaNavigation].
 *
 * You can also use [MapVinaNavigation] directly, but this leads to more configuration.
 *
 * Currently the only difference is, that the location engine is created depending on
 * your dependencies.
 */
class AndroidMapVinaNavigation(
    context: Context,
    options: MapVinaNavigationOptions = MapVinaNavigationOptions(),
    locationEngine: LocationEngine = LocationEngineProvider.getBestLocationEngine(context),
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
)
