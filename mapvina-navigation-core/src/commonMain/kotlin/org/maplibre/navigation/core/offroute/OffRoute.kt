package com.mapvina.navigation.core.offroute

import com.mapvina.navigation.core.location.Location
import com.mapvina.navigation.core.navigation.MapVinaNavigationOptions
import com.mapvina.navigation.core.routeprogress.RouteProgress

fun interface OffRoute {

    fun isUserOffRoute(location: Location, routeProgress: RouteProgress, options: MapVinaNavigationOptions): Boolean
}
