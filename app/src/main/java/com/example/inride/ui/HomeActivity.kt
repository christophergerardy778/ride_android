package com.example.inride.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.inride.R
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse

import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity(), OnMapReadyCallback,
    LocationEngineCallback<LocationEngineResult> {

    private var isLoaded = false
    private val intervalInMilliseconds = 1000L
    private val maxWaitTime = intervalInMilliseconds * 5

    private lateinit var navigationRoute: NavigationMapRoute
    private lateinit var mapBoxMap: MapboxMap
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_token))
        setContentView(R.layout.activity_home)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapBoxMap = mapboxMap

        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(style: Style) {
        val locationComponent = mapBoxMap.locationComponent

        val locationComponentActivationOptions = LocationComponentActivationOptions
            .builder(this, style)
            .useDefaultLocationEngine(true)
            .build()

        locationComponent.activateLocationComponent(locationComponentActivationOptions)
        locationComponent.isLocationComponentEnabled = true
        locationComponent.renderMode = RenderMode.COMPASS

        getLocationEngine()
        setMapStyle()
    }

    private fun setMapStyle() {
        val uiSettings = mapBoxMap.uiSettings
        uiSettings.isCompassEnabled = false
        uiSettings.isRotateGesturesEnabled = false
        uiSettings.isAttributionEnabled = false
    }

    @SuppressLint("MissingPermission")
    private fun getLocationEngine() {
        val locationEngine = LocationEngineProvider.getBestLocationEngine(this)

        val request = LocationEngineRequest.Builder(intervalInMilliseconds)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(maxWaitTime)
            .build()

        locationEngine.requestLocationUpdates(request, this, Looper.getMainLooper())
        locationEngine.getLastLocation(this)
    }

    override fun onSuccess(p0: LocationEngineResult?) {
        if (p0 != null && p0.lastLocation != null) {
            if (!isLoaded) {
                isLoaded = true

                val position = LatLng(p0.lastLocation!!.latitude, p0.lastLocation!!.longitude)

                val cameraPosition = CameraPosition.Builder()
                    .zoom(16.0)
                    .target(position)
                    .build()

                mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                getRoute(p0.lastLocation!!.latitude, p0.lastLocation!!.longitude)
            }
        }
    }

    private fun getRoute(lat: Double, lon: Double) {
        val client = MapboxDirections.builder()
            .origin(Point.fromLngLat(lat, lon))
            .destination(Point.fromLngLat(lat -1, lon -1))
            .steps(true)
            .accessToken(getString(R.string.mapbox_token))
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .build()

        client.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {
                if (response.body() == null) {
                    Log.d("magia", "error general")
                    return
                }
                else if (response.body()!!.routes().size < 1) {
                    Log.d("magia", "no hay rutas")
                    return
                }

                val route = response.body()!!.routes()[0]

                if (::navigationRoute.isInitialized) {
                    navigationRoute.removeRoute()
                } else {
                    navigationRoute = NavigationMapRoute(null, mapView, mapBoxMap, R.style.NavigationMapRoute)
                }

                if (route != null) {
                    Log.d("suc", "success")
                    navigationRoute.addRoute(route)
                }

            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Log.d("maperror", "error")
            }

        })
    }

    override fun onFailure(p0: Exception) {
        Log.d("ERROR MAP", p0.localizedMessage!!)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
}