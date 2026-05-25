package io.github.mapvina.navigation.core.navigation

import co.touchlab.kermit.Logger
import io.github.mapvina.spatialk.geojson.LineString
import io.github.mapvina.spatialk.geojson.Point
import io.github.mapvina.navigation.core.location.Location
import io.github.mapvina.navigation.core.milestone.Milestone
import io.github.mapvina.navigation.core.models.DirectionsRoute
import io.github.mapvina.navigation.core.models.LegAnnotation
import io.github.mapvina.navigation.core.models.LegStep
import io.github.mapvina.navigation.core.models.RouteLeg
import io.github.mapvina.navigation.core.models.StepIntersection
import io.github.mapvina.navigation.core.offroute.OffRouteCallback
import io.github.mapvina.navigation.core.offroute.OffRouteDetector
import io.github.mapvina.navigation.core.routeprogress.CurrentLegAnnotation
import io.github.mapvina.navigation.core.routeprogress.RouteProgress
import io.github.mapvina.navigation.core.utils.Constants
import io.github.mapvina.navigation.core.utils.MathUtils
import io.github.mapvina.spatialk.polyline.PolylineEncoding
import io.github.mapvina.spatialk.turf.measurement.distance
import io.github.mapvina.spatialk.turf.measurement.length
import io.github.mapvina.spatialk.turf.misc.nearestPointTo
import io.github.mapvina.spatialk.turf.misc.slice
import io.github.mapvina.spatialk.units.International.Kilometers
import io.github.mapvina.spatialk.units.International.Meters
import io.github.mapvina.navigation.core.models.StepManeuver
import kotlin.jvm.JvmStatic

/**
 * This contains several single purpose methods that help out when a new location update occurs and
 * calculations need to be performed on it.
 */
object NavigationHelper {
    private const val FIRST_INTERSECTION = 0
    private const val ZERO_METERS = 0.0
    private const val TWO_POINTS = 2

    @JvmStatic
    fun buildSnappedLocation(
        mapVinaNavigation: MapVinaNavigation,
        snapToRouteEnabled: Boolean,
        rawLocation: Location,
        routeProgress: RouteProgress,
        userOffRoute: Boolean
    ): Location {
        val location = if (!userOffRoute && snapToRouteEnabled) {
            mapVinaNavigation.snapEngine.getSnappedLocation(rawLocation, routeProgress)
        } else {
            rawLocation
        }
        return location
    }

    /**
     * Calculates the distance remaining in the step from the current users snapped position, to the
     * next maneuver position.
     *
     * If the user is more than 1km away from the route, we are returning the total step distance.
     */
    @JvmStatic
    fun stepDistanceRemaining(
        location: Location,
        legIndex: Int,
        stepIndex: Int,
        directionsRoute: DirectionsRoute,
        stepPoints: List<Point>
    ): Double {
        // If the linestring coordinate size is less than 2,the distance remaining is zero.
        if (stepPoints.size < 2) {
            return 0.0
        }

        val stepLineString = LineString(stepPoints.map { it.coordinates })

        // Uses Turf's pointOnLine, which takes a Point and a LineString to calculate the closest
        // Point on the LineString.
        val snappedPositionFeature = stepLineString.nearestPointTo(location.point.coordinates)
        val snappedPosition = snappedPositionFeature.geometry

        // Check distance to route line, if it's too high, it makes no sense to snap and we assume the step distance is the whole distance of the step
        val dist = distance(location.point.coordinates, snappedPosition.coordinates).toDouble(Kilometers)
        if (dist > 1) {
            Logger.d { "Distance to step is larger than 1km, so we won't advance the step, distance: $dist km" }
            return stepLineString.length().toDouble(Meters)
        }

        val steps = directionsRoute.legs[legIndex].steps
        val nextManeuverPosition = nextManeuverPosition(
            stepIndex,
            steps,
            stepPoints
        )

        // When the coordinates are empty, no distance can be calculated
        if (nextManeuverPosition == null) {
            return 0.0
        }

        // If the users snapped position equals the next maneuver position
        if (snappedPosition == nextManeuverPosition) {
            return 0.0
        }

        val slicedLine = stepLineString.slice(
            snappedPosition.coordinates,
            nextManeuverPosition.coordinates
        )
        return slicedLine.length().toDouble(Meters)
    }

    /**
     * Takes in the already calculated step distance and iterates through the step list from the
     * step index value plus one till the end of the leg.
     */
    @JvmStatic
    fun legDistanceRemaining(
        stepDistanceRemaining: Double,
        legIndex: Int,
        stepIndex: Int,
        directionsRoute: DirectionsRoute
    ): Double {
        return stepDistanceRemaining +
                directionsRoute.legs[legIndex].steps
                    .drop(stepIndex + 1)
                    .sumOf(LegStep::distance)
    }

    /**
     * Takes in the leg distance remaining value already calculated and if additional legs need to be
     * traversed along after the current one, adds those distances and returns the new distance.
     * Otherwise, if the route only contains one leg or the users on the last leg, this value will
     * equal the leg distance remaining.
     */
    @JvmStatic
    fun routeDistanceRemaining(
        legDistanceRemaining: Double,
        legIndex: Int,
        directionsRoute: DirectionsRoute
    ): Double {
        if (directionsRoute.legs.size < 2) {
            return legDistanceRemaining
        }

        return legDistanceRemaining +
                directionsRoute.legs
                    .drop(legIndex + 1)
                    .sumOf(RouteLeg::distance)
    }

    /**
     * Checks whether the user's bearing matches the next step's maneuver provided bearingAfter
     * variable. This is one of the criteria's required for the user location to be recognized as
     * being on the next step or potentially arriving.
     *
     *
     * If the expected turn angle is less than the max turn completion offset, this method will
     * wait for the step distance remaining to be 0.  This way, the step index does not increase
     * prematurely.
     *
     * @param userLocation          the location of the user
     * @param previousRouteProgress used for getting the most recent route information
     * @return boolean true if the user location matches (using a tolerance) the final heading
     * @since 0.2.0
     */
    @JvmStatic
    fun checkBearingForStepCompletion(
        userLocation: Location,
        previousRouteProgress: RouteProgress,
        stepDistanceRemaining: Double,
        maxTurnCompletionOffset: Double
    ): Boolean {
        return previousRouteProgress.currentLegProgress.upComingStep?.maneuver?.let { maneuver ->
            val initialBearingNormalized = MathUtils.wrap(maneuver.bearingBefore, 0.0, 360.0)
            val finalBearingNormalized = MathUtils.wrap(maneuver.bearingAfter, 0.0, 360.0)

            // Bearings need to be normalized so when the bearingAfter is 359 and the user heading is 1, we
            // count this as within the maxTurnCompletionOffset.
            val expectedTurnAngle =
                MathUtils.differenceBetweenAngles(initialBearingNormalized, finalBearingNormalized)

            val userBearingNormalized = MathUtils.wrap(userLocation.bearing?.toDouble() ?: 0.0, 0.0, 360.0)
            val userAngleFromFinalBearing =
                MathUtils.differenceBetweenAngles(finalBearingNormalized, userBearingNormalized)

            if (expectedTurnAngle <= maxTurnCompletionOffset) {
                stepDistanceRemaining == 0.0
            } else {
                userAngleFromFinalBearing <= maxTurnCompletionOffset
            }
        } ?: false
    }

    /**
     * This is used when a user has completed a step maneuver and the indices need to be incremented.
     * The main purpose of this class is to determine if an additional leg exist and the step index
     * has met the first legs total size, a leg index needs to occur and step index should be reset.
     * Otherwise, the step index is incremented while the leg index remains the same.
     *
     *
     * Rather than returning an int array, a new instance of Navigation Indices gets returned. This
     * provides type safety and making the code a bit more readable.
     *
     *
     * @param routeProgress   need a routeProgress in order to get the directions route leg list size
     * @param previousIndices used for adjusting the indices
     * @return a [NavigationIndices] object which contains the new leg and step indices
     */
    @JvmStatic
    fun increaseIndex(
        routeProgress: RouteProgress,
        previousIndices: NavigationIndices
    ): NavigationIndices {
        val route = routeProgress.directionsRoute
        val routeLegSize = route.legs.size
        var indices: NavigationIndices = previousIndices

        val legStepSize = route.legs[indices.legIndex].steps.size
        val previousLegIndex = indices.legIndex
        val previousStepIndex = indices.stepIndex

        val isOnLastLeg = previousLegIndex == routeLegSize - 1
        val isOnLastStep = previousStepIndex == legStepSize - 1

        indices = when {
            isOnLastStep && !isOnLastLeg -> NavigationIndices(previousLegIndex + 1, 0)
            // It's the last step of the last leg. There's nowhere to go.
            isOnLastStep -> return indices
            else -> NavigationIndices(previousLegIndex, previousStepIndex + 1)
        }

        // Then skip any zero-distance legs (waypoint legs with no actual travel)
        while (indices.legIndex < routeLegSize - 1 && route.legs[indices.legIndex].distance <= 0.0) {
            indices = NavigationIndices(indices.legIndex + 1, 0)
        }

        return indices
    }

    /**
     * Given the current [DirectionsRoute] and leg / step index,
     * return a list of [Point] representing the current step.
     *
     *
     * This method is only used on a per-step basis as [PolylineEncoding.decode]
     * can be a heavy operation based on the length of the step.
     *
     *
     * Returns null if index is invalid.
     *
     * @param directionsRoute for list of steps
     * @param legIndex        to get current step list
     * @param stepIndex       to get current step
     * @return list of [Point] representing the current step
     */
    @JvmStatic
    fun decodeStepPoints(
        directionsRoute: DirectionsRoute, currentPoints: List<Point>,
        legIndex: Int, stepIndex: Int
    ): List<Point> {
        return directionsRoute.legs
            .getOrNull(legIndex)
            ?.steps
            ?.getOrNull(stepIndex)
            ?.let { step ->
                PolylineEncoding.decode(step.geometry, Constants.PRECISION_6).map { Point(it) }
            }
            ?: currentPoints
    }

    /**
     * Given a current and upcoming step, this method assembles a list of [StepIntersection]
     * consisting of all of the current step intersections, as well as the first intersection of
     * the upcoming step (if the upcoming step isn't null).
     *
     * @param currentStep  for intersections list
     * @param upcomingStep for first intersection, if not null
     * @return complete list of intersections
     * @since 0.13.0
     */
    @JvmStatic
    fun createIntersectionsList(
        currentStep: LegStep,
        upcomingStep: LegStep?
    ): List<StepIntersection> {
        return currentStep.intersections
            ?.plus(
                listOfNotNull(
                    upcomingStep?.intersections?.firstOrNull()
                )
            )
            ?: emptyList()
    }

    /**
     * Creates a list of pairs [StepIntersection] and double distance in meters along a step.
     *
     *
     * Each pair represents an intersection on the given step and its distance along the step geometry.
     *
     *
     * The first intersection is the same point as the first point of the list of step points, so will
     * always be zero meters.
     *
     * @param stepPoints    representing the step geometry
     * @param intersections along the step to be measured
     * @return list of measured intersection pairs
     * @since 0.13.0
     */
    @JvmStatic
    fun createDistancesToIntersections(
        stepPoints: List<Point>,
        intersections: List<StepIntersection>
    ): Map<StepIntersection, Double> {
        // Require at minimum two points
        if (stepPoints.size < TWO_POINTS) {
            return emptyMap()
        }

        val stepLineString = LineString(stepPoints.map { it.coordinates })
        val distancesToIntersections = mutableMapOf<StepIntersection, Double>()
        for (intersection in intersections) {
            val intersectionPoint = intersection.location
            if (stepPoints.first() == intersectionPoint) {
                distancesToIntersections[intersection] = ZERO_METERS
            } else {
                val beginningLineString =
                    stepLineString.slice(stepPoints.first().coordinates, intersectionPoint.coordinates)
                val distanceToIntersectionInMeters =
                    beginningLineString.length().toDouble(Meters)
                distancesToIntersections[intersection] = distanceToIntersectionInMeters
            }
        }
        return distancesToIntersections
    }

    /**
     * Based on the list of measured intersections and the step distance traveled, finds
     * the current intersection a user is traveling along.
     *
     * @param intersections         along the step
     * @param measuredIntersections measured intersections along the step
     * @param stepDistanceTraveled  how far the user has traveled along the step
     * @return the current step intersection
     * @since 0.13.0
     */
    @JvmStatic
    fun findCurrentIntersection(
        intersections: List<StepIntersection>,
        measuredIntersections: Map<StepIntersection, Double>,
        stepDistanceTraveled: Double
    ): StepIntersection? = findCurrentIntersection(
        intersections,
        measuredIntersections.toList(),
        stepDistanceTraveled
    )

    @JvmStatic
    fun findCurrentIntersection(
        intersections: List<StepIntersection>,
        measuredIntersections: List<Pair<StepIntersection?, Double>>,
        stepDistanceTraveled: Double
    ): StepIntersection? {
        for (measuredIntersection in measuredIntersections) {
            if (measuredIntersection.first == null) return intersections[0]
            val intersectionDistance = measuredIntersection.second
            val intersectionIndex = measuredIntersections.indexOf(measuredIntersection)
            val nextIntersectionIndex: Int = intersectionIndex + 1
            val measuredIntersectionSize = measuredIntersections.size
            val hasValidNextIntersection = nextIntersectionIndex < measuredIntersectionSize

            if (hasValidNextIntersection) {
                val nextIntersectionDistance = measuredIntersections[nextIntersectionIndex].second
                if (stepDistanceTraveled > intersectionDistance && stepDistanceTraveled < nextIntersectionDistance) {
                    return measuredIntersection.first
                }
            } else if (stepDistanceTraveled > measuredIntersection.second) {
                return measuredIntersection.first
            } else {
                return measuredIntersections[FIRST_INTERSECTION].first
            }
        }
        return intersections[FIRST_INTERSECTION]
    }

    /**
     * Based on the current intersection index, add one and try to get the upcoming.
     *
     *
     * If there is not an upcoming intersection on the step, check for an upcoming step and
     * return the first intersection from the upcoming step.
     *
     * @param intersections       for the current step
     * @param upcomingStep        for the first intersection if needed
     * @param currentIntersection being traveled along
     * @return the upcoming intersection on the step
     * @since 0.13.0
     */
    @JvmStatic
    fun findUpcomingIntersection(
        intersections: List<StepIntersection>,
        upcomingStep: LegStep?,
        currentIntersection: StepIntersection
    ): StepIntersection? {
        return intersections.getOrNull(intersections.indexOf(currentIntersection) + 1)
            ?: upcomingStep?.intersections?.firstOrNull()
    }

    /**
     * Given a list of distance annotations, find the current annotation index. This index retrieves the
     * current annotation from any provided annotation list in [LegAnnotation].
     *
     * @param currentLegAnnotation current annotation being traveled along
     * @param leg                  holding each list of annotations
     * @param legDistanceRemaining to determine the new set of annotations
     * @return a current set of annotation data for the user's position along the route
     */
    @JvmStatic
    fun createCurrentAnnotation(
        currentLegAnnotation: CurrentLegAnnotation?,
        leg: RouteLeg,
        legDistanceRemaining: Double
    ): CurrentLegAnnotation? {
        return leg.annotation?.let { legAnnotation ->
            legAnnotation.distance?.let { distanceList ->
                findAnnotationIndex(
                    currentLegAnnotation,
                    leg,
                    legDistanceRemaining,
                    distanceList
                )?.let { annotationResult ->
                    CurrentLegAnnotation(
                        index = annotationResult.index,
                        distance = distanceList[annotationResult.index],
                        distanceToAnnotation = annotationResult.distanceToAnnotation,
                        duration = legAnnotation.duration?.get(annotationResult.index),
                        speed = legAnnotation.speed?.get(annotationResult.index),
                        maxSpeed = legAnnotation.maxSpeed?.get(annotationResult.index),
                        congestion = legAnnotation.congestion?.get(annotationResult.index),
                    )
                }
            }
        }
    }

    /**
     * This method runs through the list of milestones in [MapVinaNavigation.milestones]
     * and returns a list of occurring milestones (if any), based on their individual criteria.
     *
     * @param previousRouteProgress for checking if milestone is occurring
     * @param routeProgress         for checking if milestone is occurring
     * @param mapVinaNavigation      for list of milestones
     * @return list of occurring milestones
     */
    @JvmStatic
    fun checkMilestones(
        previousRouteProgress: RouteProgress?,
        routeProgress: RouteProgress,
        mapVinaNavigation: MapVinaNavigation
    ): List<Milestone> {
        return mapVinaNavigation.milestones
            .filter { m -> m.isOccurring(previousRouteProgress, routeProgress) }
    }

    /**
     * This method checks if off route detection is enabled or disabled.
     *
     *
     * If enabled, the off route engine is retrieved from [MapVinaNavigation] and
     * [OffRouteDetector.isUserOffRoute] is called
     * to determine if the location is on or off route.
     *
     * @param mapVinaNavigation current running navigation instance
     * @param location          new location
     * @param routeProgress    to be used in off route check
     * @param callback         only used if using our default [OffRouteDetector]
     * @return true if on route, false otherwise
     */
    @JvmStatic
    fun isUserOffRoute(
        mapVinaNavigation: MapVinaNavigation,
        location: Location,
        routeProgress: RouteProgress,
        callback: OffRouteCallback
    ): Boolean {
        val options = mapVinaNavigation.options
        if (!options.enableOffRouteDetection) {
            return false
        }

        val offRouteEngine = mapVinaNavigation.offRouteEngine
        (offRouteEngine as? OffRouteDetector)?.callback = callback

        return offRouteEngine.isUserOffRoute(
            location,
            routeProgress,
            options
        )
    }

    @Suppress("unused")
    @JvmStatic
    fun shouldCheckFasterRoute(navigationLocationUpdate: NavigationLocationUpdate, routeProgress: RouteProgress): Boolean {
        val fasterRouteEngine = navigationLocationUpdate.mapVinaNavigation.fasterRouteEngine
        return fasterRouteEngine.shouldCheckFasterRoute(
            navigationLocationUpdate.location,
            routeProgress
        )
    }

    /**
     * Retrieves the next steps maneuver position if one exist, otherwise it decodes the current steps
     * geometry and uses the last coordinate in the position list.
     */
    @JvmStatic
    fun nextManeuverPosition(stepIndex: Int, steps: List<LegStep>, coords: List<Point?>): Point? {
        // If there is an upcoming step, use it's maneuver as the position.
        if (steps.size > (stepIndex + 1)) {
            return steps[stepIndex + 1].maneuver.location
        }

        return coords.lastOrNull()
    }

    private fun findAnnotationIndex(
        currentLegAnnotation: CurrentLegAnnotation?,
        leg: RouteLeg,
        legDistanceRemaining: Double,
        distanceAnnotationList: List<Double>
    ): AnnotationResult? {
        val legDistances: List<Double> = ArrayList(distanceAnnotationList)
        val distanceTraveled = leg.distance - legDistanceRemaining
        val distanceIndex = currentLegAnnotation?.index ?: 0

        var annotationDistancesTraveled = currentLegAnnotation?.distanceToAnnotation ?: 0.0
        for (i in distanceIndex until legDistances.size) {
            val distance = legDistances[i]
            annotationDistancesTraveled += distance
            if (annotationDistancesTraveled > distanceTraveled || i == legDistances.size - 1) {
                val distanceToAnnotation = annotationDistancesTraveled - distance
                return AnnotationResult(i, distanceToAnnotation)
            }
        }

        return null
    }

    private data class AnnotationResult(
        val index: Int,
        val distanceToAnnotation: Double
    )
}
