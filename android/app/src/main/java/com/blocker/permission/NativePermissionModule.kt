// package com.blocker.permission

// import android.content.Context
// import com.facebook.react.bridge.Promise
// import android.net.Uri
// import android.provider.Settings
// import android.content.Intent
// import com.facebook.react.bridge.ReactMethod
// import com.facebook.react.bridge.ReactApplicationContext
// import com.facebook.react.bridge.ReactContextBaseJavaModule
// import android.util.Log
// import androidx.core.app.ActivityCompat
// import android.Manifest


// class NativePermissionModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {


//     companion object {
//         private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
//     }

//     override fun getName(): String {
//         return "NativePermission"
//     }

//      private fun isOverlayPermissionGranted(context: Context): Boolean {
//         return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//             Settings.canDrawOverlays(context)
//         } else {
//             true 
//         }
//     }

//     @ReactMethod
//     fun requestOverlayPermission(promise: Promise){
//        val context = reactApplicationContext

//         if (!isOverlayPermissionGranted(context)) {     
//             val intent = Intent(
//                 Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                 Uri.parse("package:com.blocker")
//             )
//             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) 
//             context.startActivity(intent)
           
//             promise.resolve("Overlay permission requested.")
//         } else {
//             promise.resolve("Overlay permission already granted.")
//         }
//     }

//     @ReactMethod
//     fun requestLocationPermission() {
//         val activity = currentActivity ?: return

//         // Request permission
//         ActivityCompat.requestPermissions(
//             activity,
//             arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//             LOCATION_PERMISSION_REQUEST_CODE
//         )
//     }

    
// }



package com.blocker.permission

import android.content.Context
import com.facebook.react.bridge.Promise
import android.net.Uri
import android.provider.Settings
import android.content.Intent
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import android.util.Log
import androidx.core.app.ActivityCompat
import android.Manifest
import android.os.Build
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat

class NativePermissionModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1002
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1003
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1004
    }

    override fun getName(): String {
        return "NativePermission"
    }

    private fun isOverlayPermissionGranted(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    @ReactMethod
    fun requestOverlayPermission(promise: Promise) {
        val context = reactApplicationContext

        if (!isOverlayPermissionGranted(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:com.blocker")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

            promise.resolve("Overlay permission requested.")
        } else {
            promise.resolve("Overlay permission already granted.")
        }
    }

    @ReactMethod
    fun requestLocationPermission() {
        val activity = currentActivity ?: return

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @ReactMethod
    fun requestCameraPermission(promise: Promise) {
        val activity = currentActivity ?: return
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            promise.resolve("Camera permission already granted.")
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
            promise.resolve("Camera permission requested.")
        }
    }

    @ReactMethod
fun requestStoragePermission(promise: Promise) {
    val activity = currentActivity ?: run {
        promise.reject("ACTIVITY_NULL", "Activity is null")
        return
    }

    // For Android 13+ (API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            promise.resolve("Storage permission already granted.")
        } else {
            val permissionListener = object : ActivityCompat.OnRequestPermissionsResultCallback {
                override fun onRequestPermissionsResult(
                    requestCode: Int,
                    permissions: Array<String>,
                    grantResults: IntArray
                ) {
                    if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
                        if (grantResults.isNotEmpty() && 
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            promise.resolve("Storage permission granted.")
                        } else {
                            promise.reject("PERMISSION_DENIED", "Storage permission denied.")
                        }
                    }
                }
            }
            
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    } else {
        // For Android 12 and below
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            promise.resolve("Storage permission already granted.")
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }
}

    @ReactMethod
    fun requestNotificationPermission(promise: Promise) {
        val activity = currentActivity ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (NotificationManagerCompat.from(activity).areNotificationsEnabled()) {
                promise.resolve("Notification permission already granted.")
            } else {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
                activity.startActivity(intent)
                promise.resolve("Notification permission requested.")
            }
        } else {
            promise.resolve("Notification permission is not required for this version.")
        }
    }



    @ReactMethod
    fun requestAccessibilityPermission(promise: Promise) {
        val context = reactApplicationContext
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
        promise.resolve("Accessibility permission requested.")
    }


}

