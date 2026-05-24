package com.mapvina.navigation.core.navigation

import android.content.Context
import com.mapvina.navigation.core.location.engine.LocationEngine
import com.mapvina.navigation.core.location.engine.LocationEngineProvider
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
