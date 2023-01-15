package com.example.filedescripter

data class MyDataClass(var fileName: String,
                       var filePath: String = "",
                       var fileType: String = "",
                       var fileLocation: String = ",",
                       var fileSize: String = "")