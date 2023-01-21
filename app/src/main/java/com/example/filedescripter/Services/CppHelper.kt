package com.example.filedescripter.Services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.filedescripter.MyDataClass

class CppHelper : ICppHelper {

    private fun convertListToString(list: List<MyDataClass>) : String {
        var str = String()
        for (li in list) {
            str += (li.fileType + ":" + li.fileSize + " ")
        }
        return str
    }

    private fun convertStringToMap(string: String) : Map<String, Long> {
        val list: List<String> = string.split(" ")
        val mapping : MutableMap<String, Long> =  mutableMapOf()
        for (li in list) {
            val pair = li.split(":")
            mapping[pair[0]] = pair[1].toLong()
        }
        return mapping
    }

    override fun doAnalysisWithJNI(list: List<MyDataClass>) : Map<String, Long> {
        val mapping : MutableMap<String, Long> =  mutableMapOf()
        for (li in list) {
            Log.d(TAG, "Anchal: doAnalysisWithJNI: ${li.fileName} ${li.fileType} and ${li.fileSize}")
            if (mapping.containsKey(li.fileType)) {
                mapping[li.fileType] = mapping[li.fileType]!! + li.fileSize.toLong()
            } else {
                mapping[li.fileType] = li.fileSize.toLong()
            }
        }
        Log.d(TAG, "Anchal: doAnalysisWithJNI: $mapping")
        return mapping
    }
}