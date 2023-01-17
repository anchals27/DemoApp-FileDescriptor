package com.example.filedescripter

import android.app.Application

private lateinit var _myApplication: MyApplication

class MyApplication : Application() {

    val dbHelper: DBHelper = DBHelper(this, null)

    companion object {
        val Instance : MyApplication
        get() = _myApplication
    }

    override fun onCreate() {
        super.onCreate()
        _myApplication = this
    }
}