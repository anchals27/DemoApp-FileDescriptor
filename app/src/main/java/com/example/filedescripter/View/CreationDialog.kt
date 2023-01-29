package com.example.filedescripter.View

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.example.filedescripter.R
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class CreationDialog {

    companion object {

        fun showDialog(layoutInflater: LayoutInflater, context: Context, curPath: String) {
            val builder = AlertDialog.Builder(context)
            val dialogLayout = layoutInflater.inflate(R.layout.name_taker_dialog, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.edit_text_dialog)
            Log.d(TAG, "Anchal: showDialog: ")
            with(builder) {
                setTitle("Enter Name")
                setPositiveButton("File") { dialog, which ->
                    Log.d(TAG, "Anchal: showDialog: Positive Button Clicked. ${editText.text}")
                    createFileFromDialog(context, editText.text, curPath)
                }
                setNeutralButton("Cancel") { dialog, which ->
                    Log.d(TAG, "Anchal: Negative button clicked")
                }
                setNegativeButton("Folder") { dialog, which ->
                    createFolderFromDialog(context, editText.text, curPath)
                }
                setView(dialogLayout)
                show()
            }
        }

        private fun createFileFromDialog(cntxt: Context, text: Editable?, curPath: String) {
            val fileName = curPath + text
            Log.d(TAG, "Anchal: createFileFromDialog: $fileName")
            if (fileName.contains(':') || fileName.contains(';')) {
                Toast.makeText(cntxt, "Name with ';' or ':' not supported", Toast.LENGTH_SHORT).show()
                return
            }
            val file = File(fileName)
            if (file.createNewFile()) {
                Toast.makeText(cntxt, "File created successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(cntxt, "File already present", Toast.LENGTH_SHORT).show()
            }
        }

        private fun createFolderFromDialog(cntxt: Context, text: Editable?, curPath: String) {
            val fileName = curPath + text
            Log.d(TAG, "Anchal: createFolderFromDialog: $fileName ${fileName.contains(':')}")
            if (fileName.contains(':') || fileName.contains(';')) {
                Log.d(TAG, "Anchal: createFolderFromDialog: name not supported")
                Toast.makeText(cntxt, "Name with ';' or ':' not supported", Toast.LENGTH_SHORT).show()
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.createDirectory(Paths.get(curPath + text))
                Log.d(TAG, "Anchal: createFolderFromDialog: created successfully")
                Toast.makeText(cntxt, "Folder created successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}