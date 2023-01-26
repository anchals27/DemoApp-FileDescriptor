package com.example.filedescripter.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.PathStackTracker
import com.example.filedescripter.ViewModels.ExplorerFragmentVM
import com.example.filedescripter.databinding.FragmentItemListBinding

/**
 * A fragment representing a list of Items.
 */
class ExplorerFragment(val pathStackTracker: PathStackTracker) : Fragment() {

    private lateinit var _binding: FragmentItemListBinding
    private lateinit var _viewModel: ExplorerFragmentVM
    private lateinit var _adapter: MyItemRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "Anchal: onCreateView: ")
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        ViewModelProviders.of(this, ExplorerFragmentVM.factory)[ExplorerFragmentVM::class.java]
            .also { _viewModel = it }

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "Anchal: onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
        _binding.recyclerView.layoutManager = LinearLayoutManager(context)
        _adapter = MyItemRecyclerViewAdapter(ArrayList(), this)
        _binding.recyclerView.adapter = _adapter
        _viewModel.listLiveData.observe(viewLifecycleOwner) {
            if (it!=null) {
                Log.d(TAG, "Anchal: onViewCreated: list being updated $it")
                _adapter.updateList(it)
            } else {
                Log.d(TAG, "Anchal: onViewCreated: list is null")
            }
        }
        _viewModel.getDirectoryList(pathStackTracker.curPath)
    }

    fun reloadList() {
        _viewModel.getDirectoryList(pathStackTracker.curPath)
    }

}