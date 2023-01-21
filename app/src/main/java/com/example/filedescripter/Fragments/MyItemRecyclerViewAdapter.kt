package com.example.filedescripter.Fragments

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filedescripter.MyDataClass
import com.example.filedescripter.databinding.ListItemBinding

class MyItemRecyclerViewAdapter(private val fileList : ArrayList<MyDataClass>) : RecyclerView.Adapter<BaseViewHolder>() {
    class ListItemVH(private val myView: ListItemBinding) : BaseViewHolder(myView.root) {
        override fun bindData(position: Int, data: Any) {
            myView.modelData = data as MyDataClass
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ListItemVH(binding)
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