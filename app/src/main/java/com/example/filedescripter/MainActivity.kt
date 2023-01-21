package com.example.filedescripter

// import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private var locationServiceProvider : LocationServiceProvider? = null
    private val STORAGE_PERMISSION_CODE = 101
    private var isDBLoaded = false
    private var curPath = Environment.getExternalStorageDirectory().path + "/"
    private lateinit var locationManager: LocationManager
    private lateinit var explorerFragment : ExplorerFragment
    private lateinit var analyticsFragment : AnalyticsFragment

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar!!.title = "File Descriptor"
        Log.d(TAG, "Anchal: onCreate: OnCreate being called ********************")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createLocationService()
        val addressBar = findViewById<TextView>(R.id.addressBar)
        addressBar.text = curPath
        explorerFragment = ExplorerFragment()
        analyticsFragment = AnalyticsFragment()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermissionForManagingAllFiles() {
        Toast.makeText(this, "Need Storage Permission", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Anchal: requestPermissionForManagingAllFiles: ${Environment.isExternalStorageManager()}")
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun requestPermissionForLocation() {
        Toast.makeText(this, "Need Location Permission", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Anchal: requestPermissionForLocation: ")
        if (SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                val uri: Uri = Uri.fromParts("package", packageName, null)
//                intent.data = uri
                startActivity(intent)
//            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Anchal: onStart")

        if (!locationServiceProvider!!.isLocationPermissionGranted()) {
            requestPermissionForLocation()
            return
        }

        if (!Environment.isExternalStorageManager()) {
            requestPermissionForManagingAllFiles()
            return
        }

        if (!locationServiceProvider!!.isLocationEnabled()) {
            locationServiceProvider!!.startIntentToEnableLocation()
            return
        }

        Log.d(TAG, "Anchal: onStart: All Permissions are granted")
        doStartupProcesses()
    }

    private fun doStartupProcesses() {
        doLoadingOfDB()
        setUpFragments()
    }

    private fun setUpFragments() {
        supportFragmentManager.beginTransaction().replace(R.id.container, explorerFragment).commit()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Explorer -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, explorerFragment).commit()
                    true
                }
                R.id.Analytics -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.container,
                        analyticsFragment
                    ).commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun createLocationService() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationServiceProvider = LocationServiceProvider(this, locationManager)
    }

    private fun doLoadingOfDB() {
        if (!isDBLoaded) {
            isDBLoaded = true
            Log.d(TAG, "Anchal: doLoadingOfDB: Loading")
            DirectoryParser.doParsingOfInternalStorage(locationServiceProvider!!)
        } else {
            Log.d(TAG, "Anchal: doLoadingOfDB: Loading avoided")
            Toast.makeText(this, "Database already loaded", Toast.LENGTH_SHORT).show()
        }
    }

//    extern fun getAnalyticsFromCpp(str: String) : String
}