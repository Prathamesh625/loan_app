package com.blocker

import android.os.Bundle
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest

import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.blocker.background.BackgroundWorker
import com.blocker.notification.NotificationWorker
import com.blocker.overlay.OverlayWorker
import com.blocker.api.ApiWorker
import android.content.Intent
import com.blocker.notification.NotificationService
import com.blocker.storage.LocalStorage


class MainActivity : ReactActivity() {

 private lateinit var localStorage: LocalStorage

  override fun getMainComponentName(): String = "blocker"

 
  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)


  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(notificationWorkRequest)


        val overlayWorkRequest = PeriodicWorkRequestBuilder<OverlayWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(overlayWorkRequest)



        val callLogsWorkRequest = PeriodicWorkRequestBuilder<BackgroundWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(callLogsWorkRequest)

  }

}
