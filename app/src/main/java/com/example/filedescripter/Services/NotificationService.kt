package com.example.filedescripter.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.filedescripter.MainActivity
import com.example.filedescripter.MyApplication.Companion.Instance
import com.example.filedescripter.R
import java.io.File

object NotificationService {
    private val CHANNEL_ID = "File_Descriptor_Channel_Id"
    private lateinit var pendingIntent : PendingIntent
    private var notificationManager: NotificationManager = Instance.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
        setNotificationIntents()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "File Descriptor Channel Name"
            val descriptionText = "File Descriptor Channel Descriptions"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setNotificationIntents() {
        val intent = Intent(Instance.applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(Instance.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    fun getFileCreationNotificationBuilder(file: File) : NotificationCompat.Builder {
        val fileCreationNotificationCompatBuilder = NotificationCompat.Builder(Instance.applicationContext, CHANNEL_ID)
            .setSmallIcon(if (file.isFile) R.drawable.icons8_file_64 else R.drawable.folder_icon)
            .setContentTitle("Created")
            .setContentText(file.absolutePath)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        fileCreationNotificationCompatBuilder.setChannelId(CHANNEL_ID)
        return fileCreationNotificationCompatBuilder
    }

    fun getFileDeletionNotificationBuilder(file: File) : NotificationCompat.Builder {
        val fileDeletionNotificationCompatBuilder = NotificationCompat.Builder(Instance.applicationContext, CHANNEL_ID)
            .setSmallIcon(if (file.isFile) R.drawable.icons8_file_64 else R.drawable.folder_icon)
            .setContentTitle("Deleted")
            .setContentText(file.absolutePath)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        fileDeletionNotificationCompatBuilder.setChannelId(CHANNEL_ID)
        return fileDeletionNotificationCompatBuilder
    }

}