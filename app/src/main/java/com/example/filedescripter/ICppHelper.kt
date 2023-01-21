package com.example.filedescripter

interface ICppHelper {
    fun doAnalysisWithJNI(list: List<MyDataClass>) : Map<String, Long>
}