package com.example.filedescripter

import android.os.Environment
import android.view.View
import android.widget.TextView
import com.example.filedescripter.Fragments.ExplorerFragment
import com.example.filedescripter.MyApplication.Companion.Instance
import java.nio.file.Path
import java.util.*

class PathStackTracker(private val addressBar: TextView) {
    private val stack = mutableListOf<String>()
    private val DEFAULT_PATH = Environment.getExternalStorageDirectory().path
    var curPath = "$DEFAULT_PATH/"

    init {
        val pathArr = DEFAULT_PATH.split("/")
        for (folderName in pathArr) {
            stack.add(folderName)
        }
        addressBar.text = curPath
    }

    fun moveToThisFolder(folderName: String) {
        stack.add(folderName)
        curPath += ("$folderName/")
        addressBar.text = curPath
    }

    fun moveBack() {
        stack.removeLast()
        curPath = String()
        for (folderName in stack) {
            curPath += ("$folderName/")
        }
        addressBar.text = curPath
    }

}