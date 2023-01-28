package com.example.filedescripter.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.filedescripter.Model.ExplorerRepo
import com.example.filedescripter.Model.IExplorerRepo
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.Model.MyDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExplorerFragmentVM(private val explorerRepo: IExplorerRepo): ViewModel() {

    private val _listLiveData = MutableLiveData<List<MyDataClass>>()

    val listLiveData: LiveData<List<MyDataClass>>
        get() = _listLiveData

    fun getDirectoryList(curPath: String) {
        val list = explorerRepo.getFilesInfoData(curPath)
        Log.d(TAG, "Anchal: getDirectoryList: $list")
        viewModelScope.launch(Dispatchers.IO) {
            _listLiveData.postValue(list)
        }
        return
    }

    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <ExplorerFragmentVM : ViewModel>
                    create(modelClass: Class<ExplorerFragmentVM>): ExplorerFragmentVM {
                return ExplorerFragmentVM(ExplorerRepo(Instance.dbHelper)) as ExplorerFragmentVM
            }
        }
    }
}