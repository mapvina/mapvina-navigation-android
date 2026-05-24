package com.mapvina.navigation.core.routeprogress

import com.mapvina.navigation.core.location.Location

fun interface ProgressChangeListener {
    fun onProgressChange(location: Location, routeProgress: RouteProgress)
}
