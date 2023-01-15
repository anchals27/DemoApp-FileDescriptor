package com.example.filedescripter;

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import java.io.File

class DirectoryParser(val dbHelper: DBHelper) {
    private val DEFAULT_PATH: String = "/storage/self/primary/"

    fun doParsingOfInternalStorage(locationServiceProvider: LocationServiceProvider) : List<MyDataClass> {

        var list = mutableListOf<MyDataClass>()
        val location = locationServiceProvider.getLastLocation()
        Log.d(TAG, "Anchal: doParsingOfInternalStorage: at ${location}")

        File(DEFAULT_PATH).walk().forEach {
            val data = MyDataClass(it.name, it.parent + "/", it.extension, location, it.totalSpace.toString())
            list.add(data)
            dbHelper.writeFileInfoToDB(data)
        }
        return list
    }

    private fun getLocation(locationServiceProvider: LocationServiceProvider) : String {
        return locationServiceProvider.getLastLocation()
    }
}
