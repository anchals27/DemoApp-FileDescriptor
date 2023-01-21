package com.example.filedescripter.Services

import com.example.filedescripter.MyDataClass

interface ICppHelper {
    fun doAnalysisWithJNI(list: List<MyDataClass>) : Map<String, Long>
}