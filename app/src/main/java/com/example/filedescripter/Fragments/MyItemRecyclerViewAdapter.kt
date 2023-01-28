package com.example.filedescripter.Fragments

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filedescripter.MyDataClass
import com.example.filedescripter.R
import com.example.filedescripter.databinding.ListItemBinding
import java.io.File
import kotlin.math.exp

class MyItemRecyclerViewAdapter(private val fileList : ArrayList<MyDataClass>,
                                private val explorerFragment: ExplorerFragment) : RecyclerView.Adapter<BaseViewHolder>() {
    class ListItemVH(private val myView: ListItemBinding,private val explorerFragment: ExplorerFragment) : BaseViewHolder(myView.root) {
        override fun bindData(position: Int, data: Any) {
            val myDataClass = data as MyDataClass
            val isDirectory = File(myDataClass.filePath + myDataClass.fileName).isDirectory
            val isFile = File(myDataClass.filePath + myDataClass.fileName).isFile
            myView.modelData = myDataClass
            setOnClickForRoot(myDataClass, isDirectory)
            setViewParameters(myDataClass, isDirectory, isFile)
        }

        private fun setOnClickForRoot(myDataClass: MyDataClass, isDirectory: Boolean) {
            myView.root.setOnClickListener {
                Log.d(TAG, "Anchal: bindData: $position ${myDataClass.fileName}")
                if (isDirectory) {
                    Log.d(TAG, "Anchal: bindData: moveToThisFolder")
                    explorerFragment.pathStackTracker.moveToThisFolder(myDataClass.fileName)
                    explorerFragment.reloadList()
                }
            }
        }

        private fun setViewParameters(myDataClass: MyDataClass, isDirectory: Boolean, isFile: Boolean) {
            myView.myImage.setImageResource(if (isDirectory) R.drawable.folder_icon
            else if (isFile) R.drawable.icons8_file_64
            else R.drawable.icons8_question_mark_48)
            myView.typeTextView.text =  if (isDirectory) "Folder"
                                        else if (isFile) myDataClass.fileType
                                        else ""
            if (isDirectory || isFile || myDataClass.fileId != "") {
                val fileSize = myDataClass.fileSize.toLong()
                var displaySize : Long = 0
                var unitType = ""
                if (fileSize < 1024) {
                    displaySize = fileSize
                    unitType = "B"
                } else if (fileSize < 1024 * 1024) {
                    displaySize = fileSize / 1024
                    unitType = "KB"
                } else {
                    displaySize = fileSize / (1024 * 1024)
                    unitType = "MB"
                }
                myView.sizeTextView.text = displaySize.toString() + unitType
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