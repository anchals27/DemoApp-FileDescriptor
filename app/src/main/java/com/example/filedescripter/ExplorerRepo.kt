package com.example.filedescripter

class ExplorerRepo(private val dbHelper: DBHelper) : IExplorerRepo{

    override fun getFilesInfoData(curPath: String) : ArrayList<MyDataClass> {
        return dbHelper.getContentsFromDB(curPath)
    }
}