package com.example.filedescripter

// import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private var locationServiceProvider : LocationServiceProvider? = null
    private var dbHelper : DBHelper? = null
    private val STORAGE_PERMISSION_CODE = 101
    private var isDBLoaded = false
    private var curPath = "/"
    lateinit var locationManager: LocationManager

    private var explorerFragment : ExplorerFragment? = null
    private var analyticsFragment : AnalyticsFragment? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar!!.title = "File Descriptor"
        Log.d(TAG, "Anchal: onCreate: OnCreate being called ********************")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        curPath = "/storage/self/primary/"
        dbHelper = DBHelper(this, null)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val addressBar = findViewById<TextView>(R.id.addressBar)
        addressBar.text = curPath
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Anchal: onStart")
        if (Environment.isExternalStorageManager()) {
            Log.d(TAG, "Anchal: onStart: All Permissions are granted")
            doStartupProcesses()
        } else {
            Log.d(TAG, "Anchal: onStart: Requesting For Permission")
            requestPermissionForManagingAllFiles()
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "Anchal: onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Anchal: onResume")
    }

    private fun doStartupProcesses() {
        createLocationService()
        doLoadingOfDB()
        setUpFragments(doReadingOfDB())
    }

    private fun setUpFragments(list: List<MyDataClass>?) {
        explorerFragment = ExplorerFragment(list!!)
        analyticsFragment = AnalyticsFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, explorerFragment!!).commit()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Explorer -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, explorerFragment!!).commit()
                    true
                }
                R.id.Analytics -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.container,
                        analyticsFragment!!
                    ).commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun doReadingOfDB() : List<MyDataClass> {
        return dbHelper!!.getContentsFromDB(curPath)
    }

    private fun createLocationService() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationServiceProvider = LocationServiceProvider(this, fusedLocationClient, this, locationManager)
    }

    private fun doLoadingOfDB() {
        if (!isDBLoaded) {
            isDBLoaded = true
            Log.d(TAG, "Anchal: doLoadingOfDB: Loading")
            DirectoryParser(dbHelper!!).doParsingOfInternalStorage(locationServiceProvider!!)
        } else {
            Log.d(TAG, "Anchal: doLoadingOfDB: Loading avoided")
            Toast.makeText(this, "Database already loaded", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Anchal: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Anchal: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Anchal: onDestroy")
    }
}