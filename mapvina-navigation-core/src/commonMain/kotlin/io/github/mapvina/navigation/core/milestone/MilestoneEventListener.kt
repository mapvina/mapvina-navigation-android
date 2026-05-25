package io.github.mapvina.navigation.core.milestone

import io.github.mapvina.navigation.core.routeprogress.RouteProgress

fun interface MilestoneEventListener {
    fun onMilestoneEvent(routeProgress: RouteProgress, instruction: String?, milestone: Milestone)
}
