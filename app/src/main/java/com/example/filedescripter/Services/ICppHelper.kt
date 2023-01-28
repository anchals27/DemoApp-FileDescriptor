package com.example.filedescripter.Services

import com.example.filedescripter.Model.MyDataClass

interface ICppHelper {
    fun doAnalysisWithJNI(list: List<MyDataClass>) : Map<String, Long>
}