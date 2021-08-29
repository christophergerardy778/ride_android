package com.example.inride.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.inride.R
import com.mapbox.android.core.location.LocationEngineProvider

import com.mapbox.mapboxsdk.Mapbox

import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions

import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

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
            .build()

        locationComponent.activateLocationComponent(locationComponentActivationOptions)
        locationComponent.isLocationComponentEnabled = true
        locationComponent.renderMode = RenderMode.COMPASS

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
        locationEngine.getLastLocation()
    }
}