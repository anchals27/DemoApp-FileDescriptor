package com.example.filedescripter

data class MyDataClass(var fileName: String,
                       var filePath: String = "",
                       var fileType: String = "",
                       var fileLocation: String = ",",
                       var fileSize: Int = 0)

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