package com.blocker.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat


import android.app.Service
import android.content.Intent
import android.database.Cursor
import android.os.IBinder
import android.provider.CallLog
import android.util.Log
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.blocker.services.CallLogService.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.pm.ServiceInfo
import retrofit2.Response
import retrofit2.create
import retrofit2.http.Body
import com.blocker.storage.LocalStorage
import android.content.Context
import com.google.gson.GsonBuilder
import retrofit2.http.Path
import retrofit2.http.PUT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar



class CallLogService : Service() {


    private lateinit var localStorage: LocalStorage

     companion object {
        private const val TAG = "CallLogService"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "CHANNEL_2"
        private const val BASE_URL = "https://blocker.divyadhara.co.in/api/"


    }

    override fun onCreate() {
        super.onCreate()
        localStorage = LocalStorage(applicationContext) 
    }

    val gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)  // Replace with your server URL
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


          val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("CallLog Service")
            .setContentText("CallLog Service is Active")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification ,ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)

        Log.d("CallLogService", "Service started")

        if (checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) == 
            PackageManager.PERMISSION_GRANTED) {
            val callLogs = fetchTodaysCallLogs()
            Log.d("CallLogService", callLogs.toString())
            callLogs.forEach { log ->
                Log.d("CallLogService", log.toString())
            }

            sendCallLogsToServer(callLogs)
        } else {
            Log.e("CallLogService", "Permission denied for reading call logs")
        }

        stopSelf()
        return START_NOT_STICKY
    }


       private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun fetchTodaysCallLogs(): List<CallLogEntry> {
        val callLogList = mutableListOf<CallLogEntry>()

        // Get the start of today in milliseconds
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis

        // Query the CallLog content provider with a filter for today's calls
        val selection = "${CallLog.Calls.DATE} >= ?"
        val selectionArgs = arrayOf(startOfToday.toString())

        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            CallLog.Calls.DATE + " DESC" // Order by date (most recent first)
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                val type = it.getInt(typeIndex)
                val date = it.getLong(dateIndex)
                val duration = it.getString(durationIndex)

                val callType = when (type) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    CallLog.Calls.REJECTED_TYPE -> "Rejected"
                    else -> "Unknown"
                }

                callLogList.add(
                    CallLogEntry(
                        number = number,
                        type = callType,
                        date = date,
                        duration = duration
                    )
                )
            }
        }

        return callLogList
    }

    private fun sendCallLogsToServer(callLogs: List<CallLogEntry>) {
        val callLogsJson = gson.toJson(callLogs)
        val epinNo = gson.fromJson(localStorage.getItem("epinNo"), String::class.java)

         if (epinNo.isNullOrEmpty()) {
            Log.e(TAG, "Epin number is null or empty. Cannot send location.")
            return
        }

        Log.d("CallLogService", "epinNo: $epinNo")
        Log.d("CallLogService", "Sending Call Logs (JSON): $callLogsJson")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val callLogRequest = CallLogRequest(callLog = callLogsJson)
                val response = apiService.sendCallLogs(epinNo, callLogRequest)

                if (response.isSuccessful) {
                    Log.d("CallLogService", "Call logs uploaded successfully: ${response.body()?.message}")
                } else {
                    Log.e("CallLogService", "Failed to upload call logs: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CallLogService", "Error while uploading call logs: ${e.message}")
            }
        }
    }


  


    // Retrofit API interface
    interface ApiService {
        @PUT("mobile/callLogLast30days/{id}")
        suspend fun sendCallLogs(@Path("id") id: String, @Body callLog:CallLogRequest): Response<UploadResponse>
    }

    // Data class for the call log entry
    data class CallLogEntry(
        val number: String,
        val type: String,
        val date: Long,
        val duration: String
    )

    data class CallLogRequest(
       val callLog:String
    )

    // Response data class
    data class UploadResponse(
        val success: Boolean,
        val message: String
    )

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
