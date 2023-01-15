package com.example.filedescripter;

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import java.io.File

class DirectoryParser(val dbHelper: DBHelper) {
    private val DEFAULT_PATH: String = "/storage/self/primary/"

    fun doParsingOfInternalStorage(locationServiceProvider: LocationServiceProvider) : List<MyDataClass> {

        Log.d(TAG, "doParsingOfInternalStorage: Hitting Here")
        var list = mutableListOf<MyDataClass>()

        File(DEFAULT_PATH).walk().forEach {
            // Make Entry to the DB
            val data = MyDataClass(it.name, it.parent + "/", it.extension, "", it.totalSpace.toString())
//            Log.d(TAG, "Anchal: DirectoryParser: name: ${it.name}," +
//                    " path: ${it.parent} size: ${it.totalSpace / (1024 * 1024 * 8)} KB, Type: ${it.extension}")

            list.add(data)
            dbHelper.writeFileInfoToDB(data)
        }
        return list
    }

    private fun getLocation(locationServiceProvider: LocationServiceProvider) : String {
        return ""
    }
}
