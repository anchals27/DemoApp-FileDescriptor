package com.example.filedescripter

// import android.Manifest
import RecursiveFileObserver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
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
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.filedescripter.Fragments.AnalyticsFragment
import com.example.filedescripter.Fragments.ExplorerFragment
import com.example.filedescripter.Services.DirectoryParser
import com.example.filedescripter.Services.LocationServiceProvider
import com.example.filedescripter.Services.NotificationService
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 101
    private var isDBLoaded = false
    private lateinit var pathStackTracker : PathStackTracker
    private lateinit var locationServiceProvider : LocationServiceProvider
    private lateinit var locationManager: LocationManager
    private lateinit var explorerFragment : ExplorerFragment
    private lateinit var analyticsFragment : AnalyticsFragment
    private lateinit var fileCreationObserver: RecursiveFileObserver
    private lateinit var notificationService: NotificationService
    private var curFragment: Fragment? = null
    private val CHANNEL_ID = "1"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar!!.title = "File Descriptor"
        Log.d(TAG, "Anchal: onCreate: OnCreate being called ********************")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createLocationService()
        val addressBar = findViewById<TextView>(R.id.addressBar)
        pathStackTracker = PathStackTracker(addressBar)
        explorerFragment = ExplorerFragment(pathStackTracker)
        analyticsFragment = AnalyticsFragment(pathStackTracker)
        notificationService = NotificationService(getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        setUpFragments()
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            fileCreationObserver = RecursiveFileObserver(pathStackTracker.curPath, locationServiceProvider,
                                                            explorerFragment, notificationService)
            fileCreationObserver.startWatching()
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setCallbackForBackButton()
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
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Anchal: onStart")

        if (!locationServiceProvider.isLocationPermissionGranted()) {
            requestPermissionForLocation()
            return
        }

        if (!Environment.isExternalStorageManager()) {
            requestPermissionForManagingAllFiles()
            return
        }

        if (!locationServiceProvider.isLocationEnabled()) {
            locationServiceProvider.startIntentToEnableLocation()
            return
        }

        Log.d(TAG, "Anchal: onStart: All Permissions are granted")
        doStartupProcesses()
        explorerFragment.reloadList()
    }

    private fun doStartupProcesses() {
        locationServiceProvider.startTrackingLocation()
        doLoadingOfDB()
    }

    private fun setUpFragments() {
        supportFragmentManager.beginTransaction().replace(R.id.container, explorerFragment).commit()
        curFragment = explorerFragment
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Explorer -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, explorerFragment).commit()
                    curFragment = explorerFragment
                    true
                }
                R.id.Analytics -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.container,
                        analyticsFragment
                    ).commit()
                    curFragment = analyticsFragment
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
            DirectoryParser.doParsingOfInternalStorage(locationServiceProvider)
        } else {
            Log.d(TAG, "Anchal: doLoadingOfDB: Loading avoided")
            Toast.makeText(this, "Database already loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCallbackForBackButton() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pathStackTracker.curPath != pathStackTracker.STARTING_PATH)
                    goBackToParent()
                else
                    finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (pathStackTracker.curPath != pathStackTracker.STARTING_PATH)
            goBackToParent()
        else {
            Toast.makeText(this, "You are at the starting of storage", Toast.LENGTH_SHORT).show()
        }
        return super.onSupportNavigateUp()
    }

    fun goBackToParent() {
        if (curFragment == analyticsFragment) {
            Log.d(TAG, "Anchal: goBackToParent: committing to analytics fragment")
            val menuItem = findViewById<BottomNavigationItemView>(R.id.Explorer)
            menuItem.performClick()
            return
        }
        pathStackTracker.moveBack()
        explorerFragment.reloadList()
        Log.d(TAG, "Anchal: onSupportNavigateUp: ")
    }

}