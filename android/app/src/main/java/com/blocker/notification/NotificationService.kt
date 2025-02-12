package com.blocker.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import android.content.pm.ServiceInfo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.blocker.storage.LocalStorage
import com.google.gson.Gson
import retrofit2.http.Path



class NotificationService : Service() {

    private val CHANNEL_ID = "foreground_service_channel"
    private val TAG = "NotificationService"
    private val handler = Handler()  
    private lateinit var localStorage: LocalStorage
    private val fetchDataRunnable = object : Runnable {
        override fun run() {
            fetchUserData()  
            handler.postDelayed(this, 5 * 60 * 1000L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Service created")
        createNotificationChannel()
        localStorage = LocalStorage(applicationContext)
        fetchUserData()  
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: Service started")

        // Start the foreground service
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("This is a foreground service notification.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        Log.d(TAG, "onStartCommand: Notification started in foreground")

     
        handler.post(fetchDataRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Service destroyed")
        handler.removeCallbacks(fetchDataRunnable)  
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind: Service bound")
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d(TAG, "createNotificationChannel: Notification channel created")
        }
    }

    private fun fetchUserData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://blocker.divyadhara.co.in/api/")  
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val gson = Gson()
        val apiService = retrofit.create(ApiService::class.java)
        val epinNo = gson.fromJson(localStorage.getItem("epinNo"), String::class.java)

        if (epinNo!= null){

            val call = apiService.getUserData(epinNo)
            call.enqueue(object : Callback<Epin> {
            override fun onResponse(call: Call<Epin>, response: Response<Epin>) {
                if (response.isSuccessful) {
                    val gson = Gson()
                    val user = response.body()
                    val userResponseString = gson.toJson(user)
                      Log.d(TAG, "user: $userResponseString")
                    localStorage.setItem("EpinData", userResponseString ?: "{}")
                } else {
                    Log.e(TAG, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Epin>, t: Throwable) {
                Log.e(TAG, "Request Failed: ${t.message}")
            }
         })
        }
    }
    
}



interface ApiService {
    @GET("mobile/epin/{id}")
    fun getUserData(@Path("id") id: String): Call<Epin>

}

data class User(val message: String, val active: Boolean, val timestamp: String)


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


data class EpinNo(
    val epinNo:String
)