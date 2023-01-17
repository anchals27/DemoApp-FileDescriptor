package com.example.filedescripter

import android.content.Context

class ExplorerRepo(val dbHelper: DBHelper) : IExplorerRepo{

    override fun getFilesInfoData(curPath: String) : ArrayList<MyDataClass> {
        return dbHelper.getContentsFromDB(curPath)
    }
}