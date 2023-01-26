package com.example.filedescripter.Fragments

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filedescripter.MyDataClass
import com.example.filedescripter.databinding.ListItemBinding
import java.io.File
import kotlin.math.exp

class MyItemRecyclerViewAdapter(private val fileList : ArrayList<MyDataClass>,
                                private val explorerFragment: ExplorerFragment) : RecyclerView.Adapter<BaseViewHolder>() {
    class ListItemVH(private val myView: ListItemBinding,private val explorerFragment: ExplorerFragment) : BaseViewHolder(myView.root) {
        override fun bindData(position: Int, data: Any) {
            val myDataClass = data as MyDataClass
            myView.modelData = myDataClass
            myView.root.setOnClickListener {
                Log.d(TAG, "Anchal: bindData: $position ${myDataClass.fileName}")
                if (File(myDataClass.filePath + myDataClass.fileName).isDirectory) {
                    Log.d(TAG, "Anchal: bindData: moveToThisFolder")
                    explorerFragment.pathStackTracker.moveToThisFolder(myDataClass.fileName)
                    explorerFragment.reloadList()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListItemVH(binding, explorerFragment)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.bindData(position, fileList[position])
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun updateList(list: List<MyDataClass>) {
        this.fileList.clear()
        Log.d(TAG, "Anchal: updateList: $list")
        this.fileList.addAll(list)
        notifyDataSetChanged()
    }
}