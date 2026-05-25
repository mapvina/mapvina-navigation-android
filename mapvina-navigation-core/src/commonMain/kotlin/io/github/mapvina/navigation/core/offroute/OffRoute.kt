package io.github.mapvina.navigation.core.offroute

import io.github.mapvina.navigation.core.location.Location
import io.github.mapvina.navigation.core.navigation.MapVinaNavigationOptions
import io.github.mapvina.navigation.core.routeprogress.RouteProgress

fun interface OffRoute {

    fun isUserOffRoute(location: Location, routeProgress: RouteProgress, options: MapVinaNavigationOptions): Boolean
}
