package com.example.filedescripter.View

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.MyDataClass
import com.example.filedescripter.PathStackTracker
import com.example.filedescripter.R
import com.example.filedescripter.Services.DirectoryParser
import com.example.filedescripter.databinding.ListItemBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MyItemRecyclerViewAdapter(private val fileList : ArrayList<MyDataClass>,
                                private val explorerFragment: ExplorerFragment) : RecyclerView.Adapter<BaseViewHolder>() {
    class ListItemVH(private val myView: ListItemBinding,private val explorerFragment: ExplorerFragment) : BaseViewHolder(myView.root) {
        override fun bindData(position: Int, data: Any) {
            val myDataClass = data as MyDataClass
            val file = File(myDataClass.filePath + myDataClass.fileName)
            val isDirectory = file.isDirectory
            val isFile = file.isFile
            myView.modelData = myDataClass
            setOnClickForRoot(myDataClass, isDirectory)
            setOnClickForDeletion(file)
            setViewParameters(myDataClass, isDirectory, isFile)
        }

        private fun setOnClickForDeletion(file: File) {
            myView.deleteImageView.setOnClickListener {
                if (PathStackTracker.curPath == Instance.STARTING_PATH) {
                    Toast.makeText(Instance.applicationContext, "This folder should not be deleted", Toast.LENGTH_SHORT).show()
                } else {
                    deleteFolder(file)
                }
                Log.d(TAG, "Anchal: setOnClickForDeletion: $file ${file.isFile} ${file.isDirectory}")
            }
        }

        private fun deleteFolder(file: File) {
            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    deleteFolder(it)
                }
            }
            file.delete()
        }

        private fun setOnClickForRoot(myDataClass: MyDataClass, isDirectory: Boolean) {
            myView.root.setOnClickListener {
                if (isDirectory) {
                    PathStackTracker.moveToThisFolder(myDataClass.fileName)
                    explorerFragment.reloadList()
                }
            }
        }

        private fun setViewParameters(myDataClass: MyDataClass, isDirectory: Boolean, isFile: Boolean) {
            val isOnStartingPath = PathStackTracker.curPath == Instance.STARTING_PATH
            myView.myImage.setImageResource(if (isDirectory) R.drawable.folder_icon
            else if (isFile) R.drawable.icons8_file_64
            else R.drawable.icons8_question_mark_48)
            myView.typeTextView.text =  if (isDirectory) "Folder"
                                        else if (isFile) myDataClass.fileType
                                        else ""
            myView.sizeTextView.text = ""
            myView.deleteImageView.isVisible = false
            if (isDirectory || isFile || myDataClass.fileId != "") {
                val fileSize = myDataClass.fileSize.toFloat()
                var displaySize : Float = 0f
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
                myView.sizeTextView.text = String.format("%.2f", displaySize) + unitType
                myView.deleteImageView.isVisible = true
                myView.deleteImageView.setImageResource(R.drawable.icons8_trash_50)
                myView.deleteImageView.alpha = if (isOnStartingPath) 0.1f else 1.0f
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