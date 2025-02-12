package com.blocker.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import android.content.pm.PackageManager

class NotificationModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private val context: Context = reactContext.applicationContext

    override fun getName(): String {
        return "NotificationModule"
    }

    @ReactMethod
    fun startNotificationService() {


        val serviceIntent = Intent(context, NotificationService::class.java)

         val notificationPermissionGranted =
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationPermissionGranted) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    @ReactMethod
    fun stopNotificationService() {
        val serviceIntent = Intent(context, NotificationService::class.java)
        context.stopService(serviceIntent)
    }
}
