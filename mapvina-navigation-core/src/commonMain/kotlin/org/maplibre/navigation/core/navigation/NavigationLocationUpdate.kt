package com.mapvina.navigation.core.navigation

import com.mapvina.navigation.core.location.Location

data class NavigationLocationUpdate(
    val location: Location,
    val mapVinaNavigation: MapVinaNavigation
)
