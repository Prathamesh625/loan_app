package com.blocker.services

import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.IBinder
import android.os.Bundle
import android.location.LocationManager
import android.util.Log
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import retrofit2.http.Body
import retrofit2.http.PUT
import android.content.pm.ServiceInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.blocker.storage.LocalStorage
import com.google.gson.Gson
import retrofit2.http.Path


class LocationService : Service() {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var localStorage: LocalStorage


    companion object {
        private const val TAG = "LocationService"
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 1000L // 10 seconds
        private const val MIN_DISTANCE_BETWEEN_UPDATES: Float = 0.1f // 10 meters
        private const val NOTIFICATION_CHANNEL_ID = "CHANNEL_2"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "LocationService Created")
        localStorage = LocalStorage(applicationContext) 

        createNotificationChannel()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d(TAG, "Location Changed: Lat: ${location.latitude}, Lon: ${location.longitude}")

                val intent = Intent("com.blocker.LOCATION_UPDATE")
                intent.putExtra("latitude", location.latitude)
                intent.putExtra("longitude", location.longitude)
                sendLocationToApi(location.latitude, location.longitude)
                sendBroadcast(intent)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                Log.d(TAG, "Provider status changed: $provider, status: $status")
            }

            override fun onProviderEnabled(provider: String) {
                Log.d(TAG, "Provider enabled: $provider")
            }

            override fun onProviderDisabled(provider: String) {
                Log.d(TAG, "Provider disabled: $provider")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Location Service is Active")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, notification,ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        }else {
            startForeground(1, notification)
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_BETWEEN_UPDATES,
                locationListener
            )
            Log.d(TAG, "Location updates started with interval: $MIN_TIME_BETWEEN_UPDATES ms and distance: $MIN_DISTANCE_BETWEEN_UPDATES m")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied: ${e.message}")
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
        Log.d(TAG, "Location updates stopped")
    }


    private fun sendLocationToApi(latitude: Double, longitude: Double) {
    val BASE_URL = "https://blocker.divyadhara.co.in/api/"




    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

        val gson = Gson()
        val epinNo = gson.fromJson(localStorage.getItem("epinNo"), String::class.java)

        if (epinNo.isNullOrEmpty()) {
            Log.e(TAG, "Epin number is null or empty. Cannot send location.")
            return
        }

        val apiService = retrofit.create(LocationApi::class.java)
        val locationRequest = LocationRequest(epinNo ,"${latitude},${longitude}")

        apiService.sendLocation(epinNo,locationRequest).enqueue(object : retrofit2.Callback<Void> {
        override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Location sent successfully")
                } else {
                    Log.e(TAG, "Failed to send location: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "Error sending location: ${t.message}")
            }
        })
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
}




data class LocationRequest(
    val id: String,
    val lastLocation:String
)

interface LocationApi {
    @PUT("mobile/updateLastLocation/{id}") 
    fun sendLocation(@Path("id") id: String, @Body locationRequest: LocationRequest): Call<Void>
}
