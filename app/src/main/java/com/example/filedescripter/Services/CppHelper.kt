package com.example.filedescripter.Services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.filedescripter.MyDataClass

class CppHelper : ICppHelper {

    companion object {
        init {
            System.loadLibrary("filedescripter")
        }

        external fun getAnalysisFromCpp(string: String) : String
    }

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
        if (list.isEmpty() || (list.size == 1 && list[0].fileId == "")) {
            return mapOf()
        }
        val queryString = convertListToString(list)
        Log.d(TAG, "Anchal: queryString: $queryString")
        val resultString = getAnalysisFromCpp(queryString)

        Log.d(TAG, "Anchal resultString: $resultString")
        val mapping = convertStringToMap(resultString)
        Log.d(TAG, "Anchal: mapping: $mapping")
        return mapping
    }
}