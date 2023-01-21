package com.example.filedescripter.Services

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.MyDataClass
import java.io.File

class DirectoryParser {
    companion object {
        private val DEFAULT_PATH: String = Environment.getExternalStorageDirectory().path + "/"
        fun doParsingOfInternalStorage(locationServiceProvider: LocationServiceProvider) : List<MyDataClass> {

            val list = mutableListOf<MyDataClass>()
            val location = locationServiceProvider.getLastLocation()
            Log.d(TAG, "Anchal: doParsingOfInternalStorage: at $location")

            File(DEFAULT_PATH).walk().forEach {
                val data = MyDataClass(it.name,
                    (it.parent?.plus(it.name)).hashCode().toString(),
                    it.parent?.plus("/") ?: DEFAULT_PATH,
                    if (it.isFile) it.extension else it.name,
                    location,
                    it.totalSpace.toString())
                list.add(data)
                Instance.dbHelper.writeFileInfoToDB(data)
            }
            Log.d(TAG, "Anchal: doParsingOfInternalStorage: $list")
            return list
        }
    }
}
