package com.example.filedescripter;

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import java.io.File

class DirectoryParser {
    var DEFAULT_PATH: String = "/storage/self/primary/"

    fun doParsingOfInternalStorage(locationServiceProvider: LocationServiceProvider) : List<MyDataClass> {
        Log.d(TAG, "doParsingOfInternalStorage: Hitting Here")
        var list = mutableListOf<MyDataClass>()
        val path = Environment.getExternalStorageDirectory().toString() + "/Documents"
        File(DEFAULT_PATH).walk().forEach {
            // Make Entry to the DB
            if (it.isDirectory)
                Log.d(TAG, "DirectoryParser: doParsingOfInternalStorage: Directory $it, size: ${it.totalSpace / (1024 * 1024 * 8)} KB, Type: Folder")
            else
                Log.d(TAG, "DirectoryParser: doParsingOfInternalStorage: File      $it, size: ${it.totalSpace / (1024 * 1024 * 8)} KB, Type: ${it.extension}")
        }
        return fileList
    }

    private fun getFileType(it: File) : String {
        return it.extension
    }

    private fun getLocation(locationServiceProvider: LocationServiceProvider) : String {
        return ""
    }
}
