package com.example.filedescripter

data class MyDataClass(var fileName: String,
                       var fileId: String = "",
                       var filePath: String = "",
                       var fileType: String = "",
                       var fileLocation: String = ",",
                       var fileSize: String = "")