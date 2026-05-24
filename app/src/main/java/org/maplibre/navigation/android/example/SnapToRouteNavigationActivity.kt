package com.mapvina.navigation.android.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapvina.navigation.core.models.DirectionsResponse
import com.mapvina.spatialk.geojson.Point
import com.mapvina.android.location.LocationComponent
import com.mapvina.android.location.LocationComponentActivationOptions
import com.mapvina.android.location.OnLocationCameraTransitionListener
import com.mapvina.android.location.modes.CameraMode
import com.mapvina.android.location.modes.RenderMode
import com.mapvina.android.maps.MapVinaMap
import com.mapvina.android.maps.OnMapReadyCallback
import com.mapvina.android.maps.Style
import com.mapvina.navigation.android.navigation.ui.v5.route.NavigationRoute
import com.mapvina.navigation.core.location.replay.ReplayRouteLocationEngine
import com.mapvina.navigation.core.models.DirectionsRoute
import com.mapvina.navigation.core.routeprogress.ProgressChangeListener
import com.mapvina.navigation.core.routeprogress.RouteProgress
import com.mapvina.navigation.core.snap.SnapToRoute
import okhttp3.Request
import com.mapvina.navigation.android.example.databinding.ActivitySnapToRouteNavigationBinding
import com.mapvina.navigation.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapvina.navigation.core.location.Location
import com.mapvina.navigation.core.location.toAndroidLocation
import com.mapvina.navigation.core.models.UnitType
import com.mapvina.navigation.core.navigation.AndroidMapVinaNavigation
import com.mapvina.navigation.core.navigation.MapVinaNavigation
import com.mapvina.navigation.core.navigation.MapVinaNavigationOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * This activity shows you simulated navigation with enabled route snapping.
 *
 * You need to do the following steps to enable route snapping:
 * 1. Disable default location engine by set [LocationComponentActivationOptions.useDefaultLocationEngine] to false.
 * 2. Get snapped location from [ProgressChangeListener] callback and set it to [LocationComponent] by [LocationComponent.forceLocationUpdate] method.
 * 3. Activate route snapping by set [MapVinaNavigationOptions.snapToRoute] to true.
 *
 * By default [SnapToRoute] is used. If you want to use your own snapping logic, you can set with [MapVinaNavigation.setSnapEngine] for your own implementation.
 */
class SnapToRouteNavigationActivity : AppCompatActivity(), OnMapReadyCallback,
    ProgressChangeListener {

    private lateinit var binding: ActivitySnapToRouteNavigationBinding
    private lateinit var mapVinaMap: MapVinaMap
    private var locationEngine: ReplayRouteLocationEngine =
        ReplayRouteLocationEngine()
    private lateinit var navigation: MapVinaNavigation
    private var route: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySnapToRouteNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigation = AndroidMapVinaNavigation(
            this,
            MapVinaNavigationOptions(snapToRoute = true)
        ).apply {
            snapEngine
            addProgressChangeListener(this@SnapToRouteNavigationActivity)
        }

        binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@SnapToRouteNavigationActivity)
        }

        binding.btnFollow.setOnClickListener {
            followLocation()
        }
    }

    private var locationComponent: LocationComponent? = null

    override fun onMapReady(mapVinaMap: MapVinaMap) {
        this.mapVinaMap = mapVinaMap
        mapVinaMap.setStyle(
            Style.Builder().fromUri(getString(R.string.map_style_light))
        ) { style ->
            enableLocationComponent(style)
            navigationMapRoute = NavigationMapRoute(navigation, binding.mapView, mapVinaMap)
            calculateRouteAndStartNavigation()
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(style: Style) {
        locationComponent = mapVinaMap.locationComponent
        mapVinaMap.locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(
                this,
                style,
            )
                .useDefaultLocationEngine(false)
                .build()
        )

        followLocation()

        mapVinaMap.locationComponent.isLocationComponentEnabled = true
    }

    private fun followLocation() {
        if (!mapVinaMap.locationComponent.isLocationComponentActivated) {
            return
        }

        mapVinaMap.locationComponent.renderMode = RenderMode.GPS
        mapVinaMap.locationComponent.setCameraMode(
            CameraMode.TRACKING_GPS,
            object :
                OnLocationCameraTransitionListener {
                override fun onLocationCameraTransitionFinished(cameraMode: Int) {
                    mapVinaMap.locationComponent.zoomWhileTracking(17.0)
                    mapVinaMap.locationComponent.tiltWhileTracking(60.0)
                }

                override fun onLocationCameraTransitionCanceled(cameraMode: Int) {}
            }
        )
    }

    private fun calculateRouteAndStartNavigation() {
        val navigationRouteBuilder = NavigationRoute.builder(this).apply {
            this.accessToken(getString(R.string.mapbox_access_token))
            this.origin(Point.fromLngLat(9.7536318, 52.3717979))
            this.addWaypoint(Point.fromLngLat(9.741052, 52.360496))
            this.destination(Point.fromLngLat(9.756259, 52.342620))
            this.voiceUnits(UnitType.METRIC)
            this.alternatives(true)
            this.baseUrl(getString(R.string.base_url))
        }

        navigationRouteBuilder.build().getRoute(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>,
            ) {
                Timber.d("Url: %s", (call.request() as Request).url.toString())
                response.body()?.let { responseBody ->
                    if (responseBody.routes.isNotEmpty()) {
                        val mapvinaResponse = DirectionsResponse.fromJson(responseBody.toJson())
                        val directionsRoute = mapvinaResponse.routes.first()
                        this@SnapToRouteNavigationActivity.route = directionsRoute
                        navigationMapRoute?.addRoutes(mapvinaResponse.routes)

                        startNavigation()
                    }
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                Timber.e(throwable, "onFailure: navigation.getRoute()")
            }
        })
    }

    fun startNavigation() {
        route?.let { route ->
            locationEngine.also { locationEngine ->
                locationEngine.assign(route)
                navigation.locationEngine = locationEngine
                navigation.startNavigation(route)
            }
        }
    }

    override fun onProgressChange(location: Location, routeProgress: RouteProgress) {
        // Update own location with the snapped location
        locationComponent?.forceLocationUpdate(location.toAndroidLocation())
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        navigation.onDestroy()
        binding.mapView.onDestroy()
    }

}
