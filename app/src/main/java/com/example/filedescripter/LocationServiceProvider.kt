package com.example.filedescripter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper.*
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener


class LocationServiceProvider(
        private val context: Context, private val fusedLocationProviderClient: FusedLocationProviderClient,
        private val activity: MainActivity)  {

    val PERMISSION_ID = 44
    private val LOCATION_NOT_AVAILABLE = "Location Not Available"

    fun getLastLocation() : String {
        var currentLocation : String = LOCATION_NOT_AVAILABLE
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation
                    .addOnCompleteListener(OnCompleteListener<Location?> { task ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            currentLocation = location.latitude.toString() + " " + location.longitude.toString()
                        }
                    })
            } else {
                Toast.makeText(context, "Please turn on your location...", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            requestPermission()
        }
        return currentLocation
    }

    // Request for location permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                                                         Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
    }

    // Check if the location permission is available or not
    private fun checkPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Check if the location is enabled or not
    private fun isLocationEnabled() : Boolean {
        val locationManager : LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, myLooper())
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation

        }
    }

}