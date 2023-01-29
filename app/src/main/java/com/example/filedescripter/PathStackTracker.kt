package com.example.filedescripter

import android.widget.TextView
import com.example.filedescripter.MyApplication.Companion.Instance

class PathStackTracker(private val addressBar: TextView) {
    private val stack = mutableListOf<String>()
    var curPath = Instance.STARTING_PATH

    init {
        val pathArr = Instance.DEFAULT_PATH.split("/")
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
        if (curPath != Instance.STARTING_PATH) {
            stack.removeLast()
            curPath = String()
            for (folderName in stack) {
                curPath += ("$folderName/")
            }
            addressBar.text = curPath
        }
    }

}