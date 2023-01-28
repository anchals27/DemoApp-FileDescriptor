import android.content.ContentValues.TAG
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.example.filedescripter.Fragments.ExplorerFragment
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.Model.MyDataClass
import com.example.filedescripter.Services.LocationServiceProvider
import com.example.filedescripter.Services.NotificationService
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
                            private val explorerFragment: ExplorerFragment,
                            private val notificationService: NotificationService,
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
        Log.d(TAG, "Anchal: doRecursiveDeletion: $file ${file.absolutePath.hashCode()}")
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                doRecursiveDeletion(it)
            } else {
                Instance.dbHelper.deleteFileFromDB(it)
            }
        }
        Instance.dbHelper.deleteFileFromDB(file)
    }

    private fun doRecursiveDeletionsAndUpdates(file: File) {
        val prevSize = getPrevSizeOfFile(file)
        doRecursiveDeletion(file)
        if (prevSize != null) {
            doCascadingUpdates(File(file.parent), -prevSize)
        }
    }

    private fun doUpdateInDBForFile(file: File) {
        val prevSize = getPrevSizeOfFile(file)
        Instance.dbHelper.updateFileSizeInDB(file)
        if (prevSize != null) {
            val newSize = file.length()
            val deltaSize = newSize - prevSize
            val parentFile = File(file.parent)
            doCascadingUpdates(parentFile, deltaSize)
        }
    }

    private fun getPrevSizeOfFile(file: File): Long? {
        val data = Instance.dbHelper.getFileInfo(file.absolutePath.hashCode().toString())
        return data?.fileSize?.toLong()
    }

    private fun doCascadingUpdates(parentFile: File, deltaSize: Long) {
        var file = parentFile
        while(file.path != Instance.DEFAULT_PATH) {
            val fileId = file.absolutePath.hashCode().toString()
            val prevSize = getPrevSizeOfFile(file) ?: break
            val newSize = prevSize + deltaSize
            Log.d(TAG, "Anchal: doCascadingUpdates: ${file.path} $deltaSize $newSize")
            Instance.dbHelper.updateOnlyFileSizeInDB(fileId, newSize.toString())
            file = File(file.parent)
        }
    }

    private fun insertFileInfoToDB(file: File, location: String) {
        val defaultPath = Environment.getExternalStorageDirectory().path + "/"
//        Log.d(TAG, "Anchal: insertFileInfoToDB1: ")
        val data = MyDataClass(file.name,
            "${file.absolutePath.hashCode()}",
            file.parent?.plus("/") ?: defaultPath,
            if (file.isFile) file.extension else file.name,
            location,
            if (file.isDirectory) "0" else file.length().toString())

//        Log.d(TAG, "Anchal: insertFileInfoToDB2: ")
        Log.d(TAG, "Anchal: insertFileInfoToDB: $data ${file.absolutePath}")
        Instance.dbHelper.writeFileInfoToDB(data)
        if (file.isFile)
            doCascadingUpdates(File(file.parent), file.length())
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
//                    doRecursiveDeletion(file)
                }
                DELETE -> {
                    Log.d(TAG, "Anchal: onEvent: DELETE ${file.path}")
                    doRecursiveDeletionsAndUpdates(file)
                    explorerFragment.reloadList()
                    triggerFileDeletionNotification(file)
                }
                CREATE -> {
                    if (file.name.contains(';') || file.name.contains(':')) {
                        Log.d(TAG, "Anchal: onEvent: Incorrect filename detected.")
                        Toast.makeText(Instance.applicationContext, "File name with ';' or ':' not supported", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Log.d(TAG, "Anchal: onEvent: CREATE ${file.absoluteFile}")
                    if (watch(file)) {
                        this@RecursiveFileObserver.startWatching(file.absolutePath)
                    }
                    Log.d(TAG, "Anchal: onEvent: CREATE insertInfo trig")
                    val location = locationServiceProvider.getLastLocation()
                    insertFileInfoToDB(file, location)
                    Log.d(TAG, "Anchal: onEvent: CREATE inserted")
                    explorerFragment.reloadList()
                    triggerFileCreationNotification(file)
                }
                MODIFY -> {
                    Log.d(TAG, "Anchal: onEvent: MODIFY ${file.absoluteFile}")
                    doUpdateInDBForFile(file)
                    explorerFragment.reloadList()
                }
            }
        }
    }

    fun triggerFileCreationNotification(file: File) {
        with(NotificationManagerCompat.from(Instance.applicationContext)) {
            notify(1, notificationService.getFileCreationNotificationBuilder(file).build())
        }
    }

    fun triggerFileDeletionNotification(file: File) {
        with(NotificationManagerCompat.from(Instance.applicationContext)) {
            notify(1, notificationService.getFileDeletionNotificationBuilder(file).build())
        }
    }

}