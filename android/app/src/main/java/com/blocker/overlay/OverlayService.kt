package com.blocker.overlay


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.pm.ServiceInfo
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.ServiceCompat
import android.widget.Button
import androidx.core.app.NotificationCompat
import com.blocker.R
import android.app.ForegroundServiceStartNotAllowedException
import android.provider.Settings
import android.util.Log



class OverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private val CHANNEL_ID = "overlay_service_channel"
    private var mediaProjection: MediaProjection? = null
    private var mediaProjectionManager: MediaProjectionManager? = null

    private val fetchDataRunnable = object : Runnable {
        override fun run() {
            showOverlay()
            handler.postDelayed(this, 5 * 60 * 1000L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        // mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            // Check for overlay permission first
            if (!Settings.canDrawOverlays(this)) {
                Log.e("OverlayService", "Overlay permission not granted")
                stopSelf()
                return START_NOT_STICKY
            }

            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("This is a foreground service notification.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build()

           startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            

            handler.post(fetchDataRunnable)
            
            Log.d("Notification", "Overlay service started")
     
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && 
                e is ForegroundServiceStartNotAllowedException) {
                Log.e("Error", "Failed to start foreground service", e)
            } else {
                Log.e("Error", "General service error", e)
            }
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }




    private fun showOverlay() {
        removeOverlay()
        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.overlay_layout, null)
 
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
                )
            params.x = 0
            params.y = 0

           params.gravity = Gravity.TOP

        try {
            windowManager?.addView(overlayView, params)
          
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeOverlay() {
        try {
            if (overlayView != null) {
                windowManager?.removeView(overlayView)
                overlayView = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(fetchDataRunnable)
        removeOverlay()
        // mediaProjection?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Overlay Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
