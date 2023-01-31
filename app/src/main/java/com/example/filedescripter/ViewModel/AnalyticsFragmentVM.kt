package com.example.filedescripter.ViewModel

import androidx.lifecycle.*
import com.example.filedescripter.*
import com.example.filedescripter.Model.ExplorerRepo
import com.example.filedescripter.Model.IExplorerRepo
import com.example.filedescripter.Services.CppHelper

class AnalyticsFragmentVM(private val explorerRepo: IExplorerRepo) : ViewModel() {

    fun getTypeToSizeMapping(curPath: String): Map<String, Long> {
        val list = explorerRepo.getFilesInfoData(curPath)
        return doAnalysis(list)
    }

    private fun doAnalysis(list: List<MyDataClass>) : Map<String, Long> {
        return CppHelper.doAnalysisWithJNI(list)
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