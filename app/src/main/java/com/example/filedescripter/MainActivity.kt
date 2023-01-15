package com.example.filedescripter

// import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

    private var locationServiceProvider : LocationServiceProvider? = null
    private var dbHelper : DBHelper = DBHelper(this, null)
    private val STORAGE_PERMISSION_CODE = 101
    private var isDBLoaded = false
    private var curPath = "/"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Anchal: onCreate: OnCreate being called ********************")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        curPath = "/storage/self/primary/"
    }

    private fun createAdapterForRecyclerView(list: List<MyDataClass>?) : MyAdapter {
        return MyAdapter(list)
    }

    private fun createRecyclerView(adapter: MyAdapter) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
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
        var list : List<MyDataClass>? = doReadingOfDB()
        createRecyclerView(createAdapterForRecyclerView(list))
    }

    private fun doReadingOfDB() : List<MyDataClass> {
        return dbHelper.getContentsFromDB(curPath)
    }

    private fun createLocationService() {
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationServiceProvider = LocationServiceProvider(this, fusedLocationClient, this)
    }

    private fun doLoadingOfDB() {
        if (!isDBLoaded) {
            isDBLoaded = true
            DirectoryParser(dbHelper).doParsingOfInternalStorage(locationServiceProvider!!)
        } else {
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