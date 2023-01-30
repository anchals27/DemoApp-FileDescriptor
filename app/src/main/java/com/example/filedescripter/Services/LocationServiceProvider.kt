package com.example.filedescripter.Services

import RecursiveFileObserver
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper.*
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*


class LocationServiceProvider(
    private val context: Context, private val locationServices: FusedLocationProviderClient
) {
    private val LOCATION_NOT_AVAILABLE = ""
    private var currentLocation : Location? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    fun getLastLocation(recursiveFileObserver: RecursiveFileObserver, fileId: String): String? {
        if (!isLocationPermissionGranted() || !isLocationEnabled()) {
            return null
        }

        locationServices.lastLocation.addOnCompleteListener { task ->
            val location: Location? = task.result
            currentLocation = location
            val strLocation = getLatLong(location)
            recursiveFileObserver.updateLocationInDB(fileId, strLocation)
            Log.d(TAG, "Anchal: getLastLocation: $location")
        }
//        currentLocation = location
        return getLatLong(currentLocation)
    }

    private fun getLatLong(location: Location?): String {
        if (location != null) {
            val latitude: Int? = (location?.latitude)?.toInt()
            val longitude: Int? = (location?.longitude)?.toInt()

            val myLocation = "$latitude, $longitude"
            Log.d(TAG, "Anchal: getLastLocation: $myLocation")
            return myLocation
        }
        return ""
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