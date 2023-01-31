package com.example.filedescripter.Services

import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.MyDataClass
import java.io.File

object DirectoryParser {

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
        if (it.name.contains(':') || it.name.contains(';'))
            return
        val data = MyDataClass(it.name,
            it.absolutePath.hashCode().toString(),
            it.parent?.plus("/") ?: Instance.DEFAULT_PATH,
            if (it.isFile) it.extension else it.name,
            "",
            size.toString())
        // Log.d(TAG, "Anchal: insertFileInfoToDB: $data ${it.absolutePath} ${it.length()}")
        Instance.dbHelper.writeFileInfoToDB(data)
    }

    fun doParsingOfInternalStorage() {
        doRecursiveWalkOnExternalStorage(File(Instance.DEFAULT_PATH))
    }
}
