package com.example.filedescripter

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
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

    var locationServiceProvider : LocationServiceProvider? = null
    private val STORAGE_PERMISSION_CODE = 101
    var isDBLoaded = false
    var DEFAULT_PATH: String = "/storage/self/primary/"
    val requiredPermissions = arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Anchal: onCreate: OnCreate being called ********************")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun loadDatabase(list: List<MyDataClass>) {
        TODO("Not yet implemented")
    }

    private fun createAdapterForRecyclerView() : MyAdapter {
        return MyAdapter(fileList)
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
        createRecyclerView(createAdapterForRecyclerView())
    }

    private fun createLocationService() {
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationServiceProvider = LocationServiceProvider(this, fusedLocationClient, this);
    }

    private fun doLoadingOfDB() {
        if (!isDBLoaded) {
            DirectoryParser().doParsingOfInternalStorage(locationServiceProvider!!)
            isDBLoaded = true
        } else {
            DirectoryParser().doParsingOfInternalStorage(locationServiceProvider!!)
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