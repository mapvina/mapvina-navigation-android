package com.mapvina.navigation.core.location.replay

import com.mapvina.navigation.core.location.Location

fun interface ReplayLocationListener {
    fun onLocationReplay(location: Location)
}
