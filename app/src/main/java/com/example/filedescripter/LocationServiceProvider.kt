package com.example.filedescripter

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener


class LocationServiceProvider(
        private val context: Context, private val fusedLocationProviderClient: FusedLocationProviderClient,
        private val activity: MainActivity, val locationManager: LocationManager) {

    val PERMISSION_ID = 44
    private val LOCATION_NOT_AVAILABLE = "Location Not Available"
    var currentLocation: Location? = null

    @SuppressLint("MissingPermission")
    fun getLastLocation(): String {
        var myLocation = LOCATION_NOT_AVAILABLE

        if (isLocationPermissionGranted()) {
            if (isLocationEnabled()) {
                val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                Log.d(TAG, "Anchal: getLastLocation: $hasGps")
                if (hasGps) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000L,
                        0f,
                        android.location.LocationListener { location: Location -> currentLocation = location }
                    )
                }

                val lastKnownLocationByGps =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                Log.d(TAG, "Anchal: getLastLocation: $lastKnownLocationByGps")
                lastKnownLocationByGps?.let {
                    currentLocation = lastKnownLocationByGps
                }
            } else {
                Toast.makeText(context, "Please turn on your location...", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            requestPermission()
        }

        if (currentLocation == null) {
            Log.d(TAG, "Anchal: getLastLocation: null")
            return LOCATION_NOT_AVAILABLE
        }
        val latitude : Int = (currentLocation!!.latitude).toInt()
        val longitude : Int = (currentLocation!!.longitude).toInt()

        myLocation = "$latitude, $longitude"
        Log.d(TAG, "Anchal: getLastLocation: $myLocation")

        return myLocation
    }

    private fun isLocationPermissionGranted() : Boolean {
        return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    // Request for location permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    // Check if the location is enabled or not
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}