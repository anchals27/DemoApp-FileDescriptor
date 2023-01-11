package com.example.filedescripter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.filedescripter.databinding.ListItemBinding

class MyAdapter(private val fileList : List<MyDataClass>) : RecyclerView.Adapter<BaseViewHolder>() {
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
}