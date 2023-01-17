package com.example.filedescripter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.filedescripter.MyApplication.Companion.Instance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExplorerFragmentVM(private val explorerRepo: IExplorerRepo): ViewModel() {

    private var curPath = "/storage/self/primary/"
    private val _listLiveData = MutableLiveData<List<MyDataClass>>(explorerRepo.getFilesInfoData(curPath))

    val listLiveData: LiveData<List<MyDataClass>>
        get() = _listLiveData

    fun getDirectoryList() {
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