package com.mapvina.navigation.core.milestone

import com.mapvina.navigation.core.json
import com.mapvina.navigation.core.BaseTest
import com.mapvina.navigation.core.milestone.Trigger.gt
import com.mapvina.navigation.core.models.DirectionsResponse
import com.mapvina.navigation.core.routeprogress.RouteProgress
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

class StepMilestoneTest : BaseTest() {

    @Test
    @Throws(Exception::class)
    fun sanity() {
        val routeProgress = buildStepMilestoneRouteProgress()
        val milestone = StepMilestone(
            identifier = 1,
            instruction = null,
            trigger = gt(TriggerProperty.STEP_DISTANCE_TOTAL_METERS, 100.0),
        )

        assertNotNull(milestone)
        assertTrue(milestone.isOccurring(routeProgress, routeProgress))
    }

    @Test
    fun identifier_doesEqualSetValue() {
            val milestone = StepMilestone(identifier = 101)
            assertEquals(101, milestone.identifier)
        }

    @Throws(Exception::class)
    private fun buildStepMilestoneRouteProgress(): RouteProgress {
        val fixtureJsonString = loadJsonFixture(ROUTE_FIXTURE)
        val response = json.decodeFromString<DirectionsResponse>(fixtureJsonString)
        val route = response.routes[0]
        val distanceRemaining = route.distance
        val legDistanceRemaining = route.legs[0].distance
        val stepDistanceRemaining = route.legs[0].steps[0].distance
        return buildTestRouteProgress(
            route, stepDistanceRemaining,
            legDistanceRemaining, distanceRemaining, 1, 0
        )
    }

    companion object {
        private const val ROUTE_FIXTURE = "directions_v5_precision_6.json"
    }
}
