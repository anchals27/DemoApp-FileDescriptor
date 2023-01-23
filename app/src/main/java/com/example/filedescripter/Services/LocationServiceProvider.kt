package com.example.filedescripter.Services

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
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


class LocationServiceProvider(
    private val context: Context, private val locationManager: LocationManager
) {
    private val LOCATION_NOT_AVAILABLE = ""

    @SuppressLint("MissingPermission")
    fun getLastLocation(): String {
        var currentLocation: Location? = null
        val myLocation: String

        if (!isLocationPermissionGranted() || !isLocationEnabled()) {
            return LOCATION_NOT_AVAILABLE
        }

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Log.d(TAG, "Anchal: getLastLocation: $hasGps")
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                0f
            ) { location: Location -> currentLocation = location }
        }

        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.d(TAG, "Anchal: getLastLocation: $lastKnownLocationByGps")
        lastKnownLocationByGps?.let {
            currentLocation = lastKnownLocationByGps
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

    fun isLocationPermissionGranted() : Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Check if the location is enabled or not
    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun startIntentToEnableLocation() {
        // Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }
}