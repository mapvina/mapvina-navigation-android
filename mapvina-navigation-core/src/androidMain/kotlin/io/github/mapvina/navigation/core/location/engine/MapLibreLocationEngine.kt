package io.github.mapvina.navigation.core.location.engine

import android.content.Context
import android.location.LocationListener
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import io.github.mapvina.android.location.engine.LocationEngineCallback
import io.github.mapvina.android.location.engine.LocationEngineRequest as MapVinaLocationRequest
import io.github.mapvina.android.location.engine.LocationEngineResult
import io.github.mapvina.android.location.engine.MapVinaFusedLocationEngineImpl
import io.github.mapvina.navigation.core.location.Location
import io.github.mapvina.navigation.core.location.toLocation
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Location engine, that using the default MapVinaLocation engine.
 *
 * @param context used to initialize the underlying [MapVinaFusedLocationEngineImpl]
 * @param looper looper that is ued by the [MapVinaFusedLocationEngineImpl] to listen on for location updates
 */
open class MapVinaLocationEngine(
    context: Context,
    private val looper: Looper?
) : LocationEngine {

    /**
     * Underlying [MapVinaFusedLocationEngineImpl] that is used to fetch location and listen to location updates.
     */
    private val mapvinaLocationEngine = MapVinaFusedLocationEngineImpl(context)

    override fun listenToLocation(request: LocationEngine.Request): Flow<Location> = callbackFlow {
        val listener = LocationListener { location -> trySend(location.toLocation()) }

        mapvinaLocationEngine.requestLocationUpdates(
            toMapVinaLocationRequest(request),
            listener,
            looper,
        )

        awaitClose { mapvinaLocationEngine.removeLocationUpdates(listener) }
    }

    override suspend fun getLastLocation(): Location? = suspendCoroutine { continuation ->
        mapvinaLocationEngine.getLastLocation(object :
            LocationEngineCallback<LocationEngineResult> {
            override fun onSuccess(locationEngineResult: LocationEngineResult) {
                continuation.resume(locationEngineResult.lastLocation?.toLocation())
            }

            override fun onFailure(exception: Exception) {
                continuation.resumeWithException(exception)
            }
        })
    }

    private fun toMapVinaLocationRequest(request: LocationEngine.Request): MapVinaLocationRequest {
        return MapVinaLocationRequest.Builder(request.maxIntervalMilliseconds)
            .setFastestInterval(request.minIntervalMilliseconds)
            .setDisplacement(request.minUpdateDistanceMeters)
            .setMaxWaitTime(request.maxUpdateDelayMilliseconds)
            .setPriority(toMapVinaPriority(request.accuracy))
            .build()
    }

    private fun toMapVinaPriority(accuracy: LocationEngine.Request.Accuracy): Int {
        return when (accuracy) {
            LocationEngine.Request.Accuracy.PASSIVE -> MapVinaLocationRequest.PRIORITY_NO_POWER
            LocationEngine.Request.Accuracy.LOW -> MapVinaLocationRequest.PRIORITY_LOW_POWER
            LocationEngine.Request.Accuracy.BALANCED -> MapVinaLocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            LocationEngine.Request.Accuracy.HIGH -> MapVinaLocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}
