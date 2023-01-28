package com.example.filedescripter.Model

import com.example.filedescripter.MyDataClass

interface IExplorerRepo {
    fun getFilesInfoData(curPath: String) : ArrayList<MyDataClass>
}