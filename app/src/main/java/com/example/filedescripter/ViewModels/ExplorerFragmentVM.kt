package com.example.filedescripter.ViewModels

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.example.filedescripter.Model.ExplorerRepo
import com.example.filedescripter.Model.IExplorerRepo
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.MyDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExplorerFragmentVM(private val explorerRepo: IExplorerRepo): ViewModel() {

    private var curPath = Environment.getExternalStorageDirectory().path + "/"
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