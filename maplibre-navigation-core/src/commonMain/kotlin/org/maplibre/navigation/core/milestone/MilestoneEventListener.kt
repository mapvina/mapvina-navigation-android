package com.mapvina.navigation.core.milestone

import com.mapvina.navigation.core.routeprogress.RouteProgress

fun interface MilestoneEventListener {
    fun onMilestoneEvent(routeProgress: RouteProgress, instruction: String?, milestone: Milestone)
}
