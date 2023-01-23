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
        private lateinit var location : String

        private fun doRecursiveWalkOnExternalStorage(file: File) : Long {
            var size : Long = 0
            file.listFiles()?.forEach {
                if (it.isDirectory) {
                    size += doRecursiveWalkOnExternalStorage(it)
                } else {
                    size += it.length()
                    insertFileInfoToDB(it, it.length())
                }
            }
            insertFileInfoToDB(file, size)
            return size
        }

        private fun insertFileInfoToDB(it: File, size: Long) {
            val data = MyDataClass(it.name,
                it.absolutePath.hashCode().toString(),
                it.parent?.plus("/") ?: DEFAULT_PATH,
                if (it.isFile) it.extension else it.name,
                location,
                size.toString())
            Log.d(TAG, "Anchal: insertFileInfoToDB: $data ${it.absolutePath}")
            Instance.dbHelper.writeFileInfoToDB(data)
        }

        fun doParsingOfInternalStorage(locationServiceProvider: LocationServiceProvider) {
            location = locationServiceProvider.getLastLocation()
            doRecursiveWalkOnExternalStorage(File(DEFAULT_PATH))
        }
    }
}
