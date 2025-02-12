package com.blocker.api

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.work.*
import android.os.Bundle
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.*


class ApiWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    
    override fun doWork(): Result {

        Log.d("Api", "Initiated...")
     
        val apiIntent = Intent(applicationContext , ApiService::class.java)
        
        applicationContext.startService(apiIntent)
   
        return Result.success()
    }
}