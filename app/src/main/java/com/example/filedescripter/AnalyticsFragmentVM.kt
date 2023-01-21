package com.example.filedescripter

import android.os.Environment
import androidx.lifecycle.*

class AnalyticsFragmentVM(private val explorerRepo: IExplorerRepo) : ViewModel() {

    private var curPath = Environment.getExternalStorageDirectory().path + "/"
    private val cppHelper = CppHelper()

    fun getTypeToSizeMapping(): Map<String, Long> {
        val list = explorerRepo.getFilesInfoData(curPath)
        return doAnalysis(list)
    }

    private fun doAnalysis(list: List<MyDataClass>) : Map<String, Long> {
        return cppHelper.doAnalysisWithJNI(list)
    }

    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <AnalyticsFragmentVM : ViewModel>
                    create(modelClass: Class<AnalyticsFragmentVM>): AnalyticsFragmentVM {
                return AnalyticsFragmentVM(ExplorerRepo(MyApplication.Instance.dbHelper)) as AnalyticsFragmentVM
            }
        }
    }
}