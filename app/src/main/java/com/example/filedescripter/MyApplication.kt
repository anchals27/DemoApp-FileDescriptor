package com.example.filedescripter

import android.app.Application
import android.os.Environment
import com.example.filedescripter.Services.LocationServiceProvider

private lateinit var _myApplication: MyApplication

class MyApplication : Application() {

    val dbHelper: DBHelper = DBHelper(this, null)
    val DEFAULT_PATH = Environment.getExternalStorageDirectory().path
    val STARTING_PATH = "$DEFAULT_PATH/"

    companion object {
        val Instance : MyApplication
        get() = _myApplication
    }

    override fun onCreate() {
        super.onCreate()
        _myApplication = this
    }
}