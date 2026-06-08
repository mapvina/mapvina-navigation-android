package io.github.mapvina.navigation.core.navigation.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import io.github.mapvina.navigation.core.location.Location
import io.github.mapvina.navigation.core.location.LocationValidator
import io.github.mapvina.navigation.core.location.engine.LocationEngine
import io.github.mapvina.navigation.core.milestone.Milestone
import io.github.mapvina.navigation.core.models.DirectionsRoute
import io.github.mapvina.navigation.core.navigation.MapVinaNavigation
import io.github.mapvina.navigation.core.navigation.NavigationEventDispatcher
import io.github.mapvina.navigation.core.navigation.NavigationHelper.buildSnappedLocation
import io.github.mapvina.navigation.core.navigation.NavigationHelper.checkMilestones
import io.github.mapvina.navigation.core.navigation.NavigationHelper.isUserOffRoute
import io.github.mapvina.navigation.core.navigation.NavigationIndices
import io.github.mapvina.navigation.core.navigation.NavigationRouteProcessor
import io.github.mapvina.navigation.core.routeprogress.RouteProgress
import io.github.mapvina.navigation.core.utils.RouteUtils

/**
 * Default implementation for [NavigationEngine] which is responsible for fetching location updates
 * and processing them to set the current navigation state.
 */
open class MapVinaNavigationEngine(
    private val mapVinaNavigation: MapVinaNavigation,
    private val routeUtils: RouteUtils,
    private val locationValidator: LocationValidator = LocationValidator(mapVinaNavigation.options.locationAcceptableAccuracyInMetersThreshold),
    private val backgroundScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val mainScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : NavigationEngine {
    private val locationEngine: LocationEngine
        get() = mapVinaNavigation.locationEngine

    private val eventDispatcher: NavigationEventDispatcher
        get() = mapVinaNavigation.eventDispatcher

    private val navigationRouteProcessor = NavigationRouteProcessor(routeUtils)
    private val processingMutex = Mutex()

    private var collectLocationJob: Job? = null

    /**
     * Start navigation for the given route.
     *
     * This call will starting listening to location updates and process this data to update to the current navigation state.
     * This will run until the [stopNavigation] is called.
     */
    override fun startNavigation(route: DirectionsRoute) {
        collectLocationJob?.cancel() // Cancel previous started run

        collectLocationJob = backgroundScope.launch {
            processLocationAndIndexUpdate(
                locationEngine.getLastLocation() ?: routeUtils.createFirstLocationFromRoute(route)
            )

            locationEngine.listenToLocation(
                LocationEngine.Request(
                    minIntervalMilliseconds = LOCATION_ENGINE_INTERVAL,
                    maxIntervalMilliseconds = LOCATION_ENGINE_INTERVAL,
                )
            ).collect(::processLocationAndIndexUpdate)
        }
    }

    /**
     * Stop and cancel the current running navigation.
     *
     * This means listening to the location updates are stopped and not consumed anymore.
     */
    override fun stopNavigation() {
        collectLocationJob?.cancel()
        collectLocationJob = null
    }

    /**
     * Check if the navigation is running
     *
     * @return true if the navigation is running, false otherwise.
     */
    override fun isRunning(): Boolean {
        return collectLocationJob?.isActive == true
    }

    /**
     * Takes a new location model and route indices runs all related engine checks against it
     * (off-route, milestones, snapped location, and faster-route).
     *
     * After running through the engines, all data is submitted to [NavigationEventDispatcher].
     *
     * @param rawLocation hold location, navigation (with options), and distances away from maneuver
     */
     suspend fun processLocationAndIndexUpdate(rawLocation: Location, index: NavigationIndices? = null) {
        processingMutex.withLock {
            // Index is set inside the mutex to avoid race conditions.
            index?.let {
                navigationRouteProcessor.setIndex(mapVinaNavigation, it)
            }

            if (!locationValidator.isValidUpdate(rawLocation)) {
                return
            }

            val routeProgress = navigationRouteProcessor
                .buildNewRouteProgress(mapVinaNavigation, rawLocation)

            val userOffRoute = determineUserOffRoute(mapVinaNavigation, rawLocation, routeProgress)
            val milestones = findTriggeredMilestones(mapVinaNavigation, routeProgress)
            val location = findSnappedLocation(
                mapVinaNavigation,
                rawLocation,
                routeProgress,
                userOffRoute
            )

            val finalRouteProgress = updateRouteProcessorWith(routeProgress)
            dispatchUpdate(userOffRoute, milestones, location, finalRouteProgress)
        }
    }

    protected fun findTriggeredMilestones(
        mapVinaNavigation: MapVinaNavigation,
        routeProgress: RouteProgress
    ): List<Milestone> {
        val previousRouteProgress = navigationRouteProcessor.routeProgress
        return checkMilestones(previousRouteProgress, routeProgress, mapVinaNavigation)
    }

    protected fun findSnappedLocation(
        mapVinaNavigation: MapVinaNavigation,
        rawLocation: Location,
        routeProgress: RouteProgress,
        userOffRoute: Boolean
    ): Location {
        val snapToRouteEnabled = mapVinaNavigation.options.snapToRoute
        return buildSnappedLocation(
            mapVinaNavigation,
            snapToRouteEnabled,
            rawLocation,
            routeProgress,
            userOffRoute
        )
    }

    protected fun determineUserOffRoute(
        mapVinaNavigation: MapVinaNavigation,
        location: Location,
        routeProgress: RouteProgress
    ): Boolean {
        val userOffRoute = isUserOffRoute(
            mapVinaNavigation,
            location,
            routeProgress,
            navigationRouteProcessor
        )
        navigationRouteProcessor.checkIncreaseIndex(mapVinaNavigation)
        return userOffRoute
    }

    protected fun updateRouteProcessorWith(routeProgress: RouteProgress): RouteProgress {
        navigationRouteProcessor.routeProgress = routeProgress
        return routeProgress
    }

    protected fun dispatchUpdate(
        userOffRoute: Boolean,
        milestones: List<Milestone>,
        location: Location,
        routeProgress: RouteProgress
    ) {
        mainScope.launch {
            dispatchRouteProgress(location, routeProgress)
            dispatchTriggeredMilestones(milestones, routeProgress)
            dispatchOffRoute(location, userOffRoute)
        }
    }

    protected fun dispatchRouteProgress(location: Location, routeProgress: RouteProgress) {
        eventDispatcher.onProgressChange(location, routeProgress)
    }

    protected fun dispatchTriggeredMilestones(
        triggeredMilestones: List<Milestone>,
        routeProgress: RouteProgress
    ) {
        for (milestone in triggeredMilestones) {
            val instruction = milestone.getInstruction()?.buildInstruction(routeProgress)
            eventDispatcher.onMilestoneEvent(routeProgress, instruction, milestone)
        }
    }

    protected fun dispatchOffRoute(location: Location, isUSerOffRoute: Boolean) {
        if (isUSerOffRoute) {
            eventDispatcher.onUserOffRoute(location)
        }
    }

    /**
     * Manually triggers a route progress update for the specified leg and step indices.
     * This method is used for waypoint skipping during active navigation.
     *
     * @param legIndex The target leg index to navigate to
     * @param stepIndex The target step index to navigate to
     */
    override fun triggerManualRouteUpdate(legIndex: Int, stepIndex: Int) {
        backgroundScope.launch {
            locationEngine.getLastLocation()?.let { currentLocation ->
                processLocationAndIndexUpdate(currentLocation, index = NavigationIndices(legIndex, stepIndex))
            }
        }
    }

    companion object {
        const val LOCATION_ENGINE_INTERVAL = 1000L
    }
}
