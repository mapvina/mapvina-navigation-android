package com.mapvina.navigation.core.offroute

import com.mapvina.navigation.core.location.Location

fun interface OffRouteListener {
    fun userOffRoute(location: Location)
}
