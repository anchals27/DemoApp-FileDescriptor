package com.example.filedescripter

interface IExplorerRepo {
    fun getFilesInfoData(curPath: String) : ArrayList<MyDataClass>
}