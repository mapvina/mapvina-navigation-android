package io.github.mapvina.navigation.core.location.replay

import io.github.mapvina.navigation.core.location.Location

fun interface ReplayLocationListener {
    fun onLocationReplay(location: Location)
}
