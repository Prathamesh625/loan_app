package com.blocker.reciever



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.content.pm.PackageManager
import com.blocker.notification.NotificationService
import com.blocker.overlay.OverlayService
import android.provider.Settings


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            
            val serviceIntent = Intent(context, NotificationService::class.java)
            val overlayService = Intent(context, OverlayService::class.java)

            val notificationPermissionGranted =
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationPermissionGranted) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            if (Settings.canDrawOverlays(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(overlayService)
                } else {
                    context.startService(overlayService)
                }
            }
        }
    }
}
