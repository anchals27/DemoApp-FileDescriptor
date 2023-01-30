package com.example.filedescripter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import android.util.Log
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        Log.d(TAG, "Anchal: onCreate: Dropping table")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + FILE_NAME + " TEXT," +
                FILE_ID + " TEXT PRIMARY KEY," +
                FILE_PATH + " TEXT," +
                FILE_SIZE + " TEXT," +
                FILE_TYPE + " TEXT," +
                FILE_LOCATION + " TEXT)")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
//        Log.d(TAG, "Anchal: query: $query")
        Log.d(TAG, "Anchal: onCreate: DB Creation Successfully")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        Log.d(TAG, "Anchal: onUpgrade: Dropping table and creating new one")
        onCreate(db)
    }

    // This method is for adding data in our database
    fun writeFileInfoToDB(data : MyDataClass){

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(FILE_NAME,       data.fileName)
        values.put(FILE_ID,         data.fileId)
        values.put(FILE_PATH,       data.filePath)
        values.put(FILE_TYPE,       data.fileType)
        values.put(FILE_LOCATION,   data.fileLocation)
        values.put(FILE_SIZE,       data.fileSize)

        val db = this.writableDatabase
        // the value of the database was very important at that time
        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
        // Log.d(TAG, "Anchal: Insert File into DB Successfully!")
    }

    private fun getDataFromDB(curDirectory: String, db: SQLiteDatabase): Cursor {
//        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $FILE_PATH = '$curDirectory'"
//         val query = "SELECT * FROM $TABLE_NAME"
        Log.d(TAG, "Anchal: getDataFromDB: $query")

//        return db.query(TABLE_NAME, arrayOf(FILE_NAME), null,
//            null, null, null, null)

        //        db.close()
        return db.rawQuery(query, null)
    }

    @SuppressLint("Range")
    fun getContentsFromDB(curDirectory: String) : ArrayList<MyDataClass> {
        val db = this.readableDatabase
        val cursor : Cursor = getDataFromDB(curDirectory, db)
        val list = ArrayList<MyDataClass>()
//         Log.d(TAG, "Anchal: getContentsFromDB: ${}")
        if (cursor.moveToFirst()) {
            do {
//                Log.d(TAG, "Anchal: getContentsFromDB: ${cursor.getColumnIndex(FILE_NAME)}")
                if (cursor.getColumnIndex(FILE_NAME) < 0 ||
                    cursor.getColumnIndex(FILE_PATH) < 0 ||
                    cursor.getColumnIndex(FILE_ID)   < 0 ||
                    cursor.getColumnIndex(FILE_SIZE) < 0 ||
                    cursor.getColumnIndex(FILE_TYPE) < 0 ||
                    cursor.getColumnIndex(FILE_LOCATION) < 0) {
//                    Log.d(TAG, "Anchal: getContentsFromDB: cursor < 0, col_count: ${cursor.columnCount}")
//                    return listOf(MyDataClass("Oops! You Ran into a problem, Col index < 0!!"))
                    return ArrayList(listOf(MyDataClass("Oops! You Ran into a problem, Col index < 0!!")))
                } else {
//                    Log.d(TAG, "Anchal: one reading adding ${index}")
//                    Log.d(TAG, "Anchal: Cursor getString: ${cursor.getString(index)} -- $curDirectory")
//                    if (cursor.getString(index) + "/" == curDirectory)
                    list.add(
                        MyDataClass(
                            fileName = cursor.getString(cursor.getColumnIndex(FILE_NAME)),
                            fileId = cursor.getString(cursor.getColumnIndex(FILE_ID)),
                            filePath = cursor.getString(cursor.getColumnIndex(FILE_PATH)),
                            fileSize = cursor.getString(cursor.getColumnIndex(FILE_SIZE)),
                            fileType = cursor.getString(cursor.getColumnIndex(FILE_TYPE)),
                            fileLocation = cursor.getString(cursor.getColumnIndex(FILE_LOCATION)),
                        )
                    )
//                     Log.d(TAG, "Anchal: one reading adding done")
                }
            } while(cursor.moveToNext())
        } else {
            return ArrayList(listOf(MyDataClass("No items to display here...")))
        }
        cursor.close()
        db.close()
        return list
    }

    @SuppressLint("Range")
    fun getFileInfo(fileId: String) : MyDataClass? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $FILE_ID = $fileId"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val myDataClass = MyDataClass(
                fileName = cursor.getString(cursor.getColumnIndex(FILE_NAME)),
                fileId = cursor.getString(cursor.getColumnIndex(FILE_ID)),
                filePath = cursor.getString(cursor.getColumnIndex(FILE_PATH)),
                fileSize = cursor.getString(cursor.getColumnIndex(FILE_SIZE)),
                fileType = cursor.getString(cursor.getColumnIndex(FILE_TYPE)),
                fileLocation = cursor.getString(cursor.getColumnIndex(FILE_LOCATION)),
            )
            cursor.close()
//            db.close()
            return myDataClass
        } else {
            cursor.close()
//            db.close()
            return null
        }
    }

    fun updateOnlyFileSizeInDB(fileId: String, fileSize: String) {
        val db = this.writableDatabase
        val query = "UPDATE $TABLE_NAME " +
                "SET $FILE_SIZE = $fileSize " +
                "WHERE $FILE_ID = '$fileId'"
        Log.d(TAG, "Anchal: Update Query: $query")
        db.execSQL(query)
//        db.close()
    }

    fun checkFileExistInDB(fileId: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT $FILE_ID FROM $TABLE_NAME WHERE $FILE_ID = $fileId"
        Log.d(TAG, "Anchal: InsertInfo: checkFileExistInDB: $query")
        val cursor = db.rawQuery(query, null)
        val ret: Boolean = cursor.moveToFirst()
        cursor.close()
        return ret
    }

    fun deleteFileFromDB(file: File) {
        val db = this.writableDatabase
        val query = "DELETE FROM $TABLE_NAME " +
                "WHERE $FILE_ID = ${file.absolutePath.hashCode()}"
        Log.d(TAG, "Anchal: Delete Query: $query")
        db.execSQL(query)
        Log.d(TAG, "Anchal: deleteFileFromDB: $file")
    }

    fun updateOnlyLocation(fileId: String, location: String) {
        val db = this.writableDatabase
        val query = "UPDATE $TABLE_NAME " +
                "SET $FILE_LOCATION = '$location' " +
                "WHERE $FILE_ID = '$fileId'"
        Log.d(TAG, "Anchal: Update Query: $query")
        db.execSQL(query)
    }


    companion object{
        // here we have defined variables for our database
        private val DATABASE_NAME = "FILE_DESCRIPTOR_DB"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "files_table"
        val FILE_NAME = "file_name"
        val FILE_ID = "file_id"
        val FILE_PATH = "file_path"
        val FILE_TYPE = "file_type"
        val FILE_LOCATION = "file_location"
        val FILE_SIZE = "file_size"
    }
}