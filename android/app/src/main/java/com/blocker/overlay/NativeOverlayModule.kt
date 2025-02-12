package com.blocker.overlay

import android.content.Context
import androidx.core.app.ActivityCompat;
import android.net.Uri
import android.provider.Settings
import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import android.util.Log
import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import com.blocker.services.LocationService
import com.blocker.services.GallaryService

class NativeOverlayModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "OverlayModule"
    }

   @ReactMethod
    fun startOverlay() {
        
        val context: Context = reactApplicationContext
        // val intent = Intent(context, OverlayService::class.java)
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        //     context.startForegroundService(intent)
        // } else {
        //     context.startService(intent)
        // }





    // val fineLocationPermission = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    // val coarseLocationPermission = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    //   val galleryPermission = context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED




    //   if (galleryPermission) {
    //         val intent = Intent(context, GallaryService::class.java)
    //         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
    //             context.startForegroundService(intent)
    //         } else {
    //             context.startService(intent)
    //         }
    //     }
         


        // if (fineLocationPermission && coarseLocationPermission) {
        //     val intent = Intent(context, LocationService::class.java)
        //     if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        //         context.startForegroundService(intent)
        //     } else {
        //         context.startService(intent)
        //     }
        // }
         


        if (Settings.canDrawOverlays(context))
            {
           val intent = Intent(context, OverlayService::class.java)
            context.startForegroundService(intent)
            } else {
            Log.e("MyService", "No permissions ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION!")
        }
        
        }

    @ReactMethod
    fun stopOverlay() {
        val context: Context = reactApplicationContext
        val intent = Intent(context, OverlayService::class.java)
        context.stopService(intent)
    }

}