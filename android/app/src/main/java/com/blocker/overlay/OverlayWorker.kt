package com.blocker.overlay

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blocker.storage.LocalStorage
import com.google.gson.Gson
import android.util.Log


class OverlayWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val localStorage = LocalStorage(applicationContext)

        val userMessageJson = localStorage.getItem("EpinData")

        Log.d("OverlayWorker" , "Overlay Worker started")

        val gson = Gson()
        val user = gson.fromJson(userMessageJson, Epin::class.java)



        if (!Settings.canDrawOverlays(applicationContext)) {
            return Result.failure()
        }

        if (user.ismBolcked) {
            val intent = Intent(applicationContext, OverlayService::class.java)
            applicationContext.startService(intent)
        } else {
            val intent = Intent(applicationContext, OverlayService::class.java)
            applicationContext.stopService(intent)
        }

        return Result.success()
    }
}

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

