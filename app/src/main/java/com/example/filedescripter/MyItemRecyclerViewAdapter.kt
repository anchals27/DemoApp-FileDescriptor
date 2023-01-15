package com.example.filedescripter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.filedescripter.databinding.ListItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(private val fileList : List<MyDataClass>?) : RecyclerView.Adapter<BaseViewHolder>() {
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
        if (fileList == null)
            holder.bindData(position, MyDataClass("NULL"))
        else
            holder.bindData(position, fileList[position])
    }

    override fun getItemCount(): Int {
        if (fileList == null)
            return 0
        return fileList.size
    }
}