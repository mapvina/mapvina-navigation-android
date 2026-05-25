package io.github.mapvina.navigation.core.offroute

import io.github.mapvina.navigation.core.routeprogress.RouteProgress

interface OffRouteCallback {

    /**
     * This callback will fire when the [OffRouteDetector] determines that the user
     * location is close enough to the upcoming [io.github.mapvina.navigation.core.models.LegStep].
     *
     * In this case, the step index needs to be increased for the next [RouteProgress] generation.
     */
    fun onShouldIncreaseIndex()

    /**
     * This callback will fire when the [OffRouteDetector] determines that the user
     * location is close enough to a [io.github.mapvina.navigation.core.models.LegStep].
     *
     * This allows to the OffRouteDetector to either go steps back or multple steps forward.
     *
     * You can use this for advanced navigation scenarios, by default you probably don't need this.
     */
    fun onShouldUpdateToIndex(legIndex: Int, stepIndex: Int)
}
