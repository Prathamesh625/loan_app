package com.blocker.notification

import android.util.Log
import android.os.Build
import androidx.work.Worker
import android.content.Context
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.work.WorkerParameters
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat




class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        

        val serviceIntent = Intent(applicationContext, NotificationService::class.java)

         val notificationPermissionGranted =
            ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationPermissionGranted) {
            applicationContext.startForegroundService(serviceIntent)
        } else {
            applicationContext.startService(serviceIntent)
        }

        return Result.success()
    }

 

 
}
