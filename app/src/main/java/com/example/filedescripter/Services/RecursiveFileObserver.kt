import android.content.ContentValues.TAG
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.MyDataClass
import com.example.filedescripter.Services.DirectoryParser
import com.example.filedescripter.Services.LocationServiceProvider
import java.io.File
import java.util.*

/**
 * A FileObserver that observes all the files/folders within given directory
 * recursively. It automatically starts/stops monitoring new folders/files
 * created after starting the watch.
 */
@RequiresApi(Build.VERSION_CODES.Q)
class RecursiveFileObserver(private val mPath: String,
                            private val locationServiceProvider: LocationServiceProvider,
                            private val mask: Int = ALL_EVENTS) :
    FileObserver(File(mPath), mask) {
    private val mObservers: MutableMap<String, FileObserver?> = HashMap()

    private fun startWatching(path: String) {
        synchronized(mObservers) {
            var observer = mObservers.remove(path)
            observer?.stopWatching()
            observer = SingleFileObserver(path, mask)
            observer.startWatching()
            mObservers.put(path, observer)
        }
    }

    override fun startWatching() {
        val stack = Stack<String>()
        stack.push(mPath)

        // Recursively watch all child directories
        while (!stack.empty()) {
            val parent = stack.pop()
            startWatching(parent)
            val path = File(parent)
            val files = path.listFiles()
            if (files != null) {
                for (file in files) {
                    if (watch(file)) {
                        stack.push(file.absolutePath)
                    }
                }
            }
        }
    }

    private fun watch(file: File): Boolean {
        return file.isDirectory && file.name != "." && file.name != ".."
    }

    private fun stopWatching(path: String) {
        synchronized(mObservers) {
            val observer = mObservers.remove(path)
            observer?.stopWatching()
        }
    }

    override fun stopWatching() {
        synchronized(mObservers) {
            for (observer in mObservers.values) {
                observer!!.stopWatching()
            }
            mObservers.clear()
        }
    }

    override fun onEvent(event: Int, path: String?) {
        // Do Nothing
    }

    private fun doRecursiveDeletion(file: File) {
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                doRecursiveDeletion(it)
            } else {
                Instance.dbHelper.deleteFileFromDB(it)
            }
        }
        Instance.dbHelper.deleteFileFromDB(file)
    }

    private fun doUpdateInDBForFile(file: File) {
        Instance.dbHelper.updateFileSizeInDB(file)
    }

    private fun insertFileInfoToDB(file: File, location: String) {
        val defaultPath = Environment.getExternalStorageDirectory().path + "/"
        Log.d(TAG, "Anchal: insertFileInfoToDB1: ")
        val data = MyDataClass(file.name,
            file.absolutePath.hashCode().toString(),
            file.parent?.plus("/") ?: defaultPath,
            if (file.isFile) file.extension else file.name,
            "location",
            file.length().toString())

        Log.d(TAG, "Anchal: insertFileInfoToDB2: ")
        Log.d(TAG, "Anchal: insertFileInfoToDB: $data ${file.absolutePath}")
        Instance.dbHelper.writeFileInfoToDB(data)
    }

    private inner class SingleFileObserver(private val filePath: String, mask: Int) :
        FileObserver(File(filePath), mask) {
        override fun onEvent(event: Int, path: String?) {
            val file: File = if (path == null) File(filePath) else File(filePath, path)

            if (file.name[0] == '.') {
                return
            }

            when (event and ALL_EVENTS) {
                DELETE_SELF -> {
                    Log.d(TAG, "Anchal: onEvent: DELETE_SELF ${file.absoluteFile}")
                    this@RecursiveFileObserver.stopWatching(filePath)
                    doRecursiveDeletion(file)
                }
                DELETE -> {
                    Log.d(TAG, "Anchal: onEvent: DELETE ${file.path}")
                    doRecursiveDeletion(file)
                }
                CREATE -> {
                    Log.d(TAG, "Anchal: onEvent: CREATE ${file.absoluteFile}")
                    if (watch(file)) {
                        this@RecursiveFileObserver.startWatching(file.absolutePath)
                    }
                    Log.d(TAG, "Anchal: onEvent: CREATE insertInfo trig")
                    val location = locationServiceProvider.getLastLocation()
                    insertFileInfoToDB(file, location)
                    Log.d(TAG, "Anchal: onEvent: CREATE inserted")
                }
                MODIFY -> {
                    Log.d(TAG, "Anchal: onEvent: MODIFY ${file.absoluteFile}")
                    doUpdateInDBForFile(file)
                }
            }
        }
    }
}