package io.github.mapvina.navigation.core.navigation

import io.github.mapvina.navigation.core.location.Location

data class NavigationLocationUpdate(
    val location: Location,
    val mapVinaNavigation: MapVinaNavigation
)
