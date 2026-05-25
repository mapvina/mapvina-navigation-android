package io.github.mapvina.navigation.core.offroute

import io.github.mapvina.navigation.core.location.Location

fun interface OffRouteListener {
    fun userOffRoute(location: Location)
}
