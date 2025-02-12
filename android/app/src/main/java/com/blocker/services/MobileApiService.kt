package com.blocker.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MobileApiService {
    @POST("mobile/callLogLast30days/{id}")
     fun sendCallLogs(
        @Path("id") userId: String,
        @Body callLogs: List<CallLogEntry>
    ): Call<ApiResponse>
}




 data class CallLogEntry(
        val number: String,
        val type: String,
        val date: Long,
        val duration: String
    )

data class ApiResponse(
    val status: String,
    val message: String
)
