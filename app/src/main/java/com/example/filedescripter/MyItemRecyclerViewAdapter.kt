package com.example.filedescripter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.filedescripter.databinding.ListItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(private val fileList : ArrayList<MyDataClass>) : RecyclerView.Adapter<BaseViewHolder>() {
    class ListItemVH(val myView: ListItemBinding) : BaseViewHolder(myView.root) {
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