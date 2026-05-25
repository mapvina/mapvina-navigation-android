package io.github.mapvina.navigation.core.routeprogress

import io.github.mapvina.navigation.core.location.Location

fun interface ProgressChangeListener {
    fun onProgressChange(location: Location, routeProgress: RouteProgress)
}
