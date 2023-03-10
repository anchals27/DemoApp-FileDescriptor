import android.content.ContentValues.TAG
import android.os.Build
import android.os.FileObserver
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.Services.LocationServiceProvider
import com.example.filedescripter.Services.NotificationService
import com.example.filedescripter.View.ExplorerFragment
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.File
import java.util.*

/**
 * A FileObserver that observes all the files/folders within given directory
 * recursively. It automatically starts/stops monitoring new folders/files
 * created after starting the watch.
 */
@RequiresApi(Build.VERSION_CODES.Q)
class RecursiveFileObserver(private val mPath: String,
                            private val fusedLocationProviderClient: FusedLocationProviderClient,
                            private val explorerFragment: ExplorerFragment,
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
        val prevSize = getPrevSizeOfFile(file)!!
        doRecursiveDeletion(file)
        doCascadingUpdates(File(file.parent!!), -prevSize)
    }

    private fun doUpdateInDBForFile(file: File) {
        val prevSize = getPrevSizeOfFile(file)
        val fileId = file.absolutePath.hashCode().toString()
        if (!Instance.dbHelper.checkFileExistInDB(fileId)) {
            insertFileInfoToDB(file)
            LocationServiceProvider.getLastLocation(this, fileId, fusedLocationProviderClient)
            return
        }
        Instance.dbHelper.updateOnlyFileSizeInDB(file.absolutePath.hashCode().toString(), file.length().toString())
        if (prevSize != null) {
            val newSize = file.length()
            val deltaSize = newSize - prevSize
            val parentFile = File(file.parent!!)
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
            Instance.dbHelper.updateOnlyFileSizeInDB(fileId, newSize.toString())
            file = File(file.parent!!)
        }
    }

    private fun insertFileInfoToDB(file: File) {
        val sizeOfFile = file.length()
        Instance.dbHelper.insertFileInfoToDB(file, sizeOfFile)
        if (file.isFile)
            doCascadingUpdates(File(file.parent!!), sizeOfFile)
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
                    triggerFileDeletionNotification(file)
                    doRecursiveDeletionsAndUpdates(file)
                    explorerFragment.reloadList()
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
                    insertFileInfoToDB(file)
                    LocationServiceProvider.getLastLocation(this@RecursiveFileObserver,
                                                            file.absolutePath.hashCode().toString(),
                                                            fusedLocationProviderClient)
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
            notify(1, NotificationService.getFileCreationNotificationBuilder(file).build())
        }
    }

    fun triggerFileDeletionNotification(file: File) {
        with(NotificationManagerCompat.from(Instance.applicationContext)) {
            notify(1, NotificationService.getFileDeletionNotificationBuilder(file).build())
        }
    }

    fun updateLocationInDB(fileId: String, location: String) {
        Instance.dbHelper.updateOnlyLocation(fileId, location)
        explorerFragment.reloadList()
    }

}