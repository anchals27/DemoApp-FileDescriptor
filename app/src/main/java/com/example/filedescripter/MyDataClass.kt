package com.example.filedescripter

data class MyDataClass(var fileName: String)

val fileList : List<MyDataClass> = listOf(
    MyDataClass("TextFile1"),
    MyDataClass("TextFile2"),
    MyDataClass("TextFile3"),
    MyDataClass("TextFile1"),
    MyDataClass("TextFile2"),
    MyDataClass("TextFile3"),
    MyDataClass("TextFile1"),
    MyDataClass("TextFile2"),
    MyDataClass("TextFile3")
)