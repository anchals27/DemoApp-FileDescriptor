package com.example.filedescripter.Model

import com.example.filedescripter.DBHelper
import com.example.filedescripter.MyDataClass

class ExplorerRepo(private val dbHelper: DBHelper) : IExplorerRepo {

    override fun getFilesInfoData(curPath: String) : ArrayList<MyDataClass> {
        return dbHelper.getContentsFromDB(curPath)
    }
}