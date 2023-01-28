package com.example.filedescripter.Model

interface IExplorerRepo {
    fun getFilesInfoData(curPath: String) : ArrayList<MyDataClass>
}