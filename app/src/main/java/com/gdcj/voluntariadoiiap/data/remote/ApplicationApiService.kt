package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Application
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApplicationApiService {
    @POST("applications")
    suspend fun createApplication(
        @Header("Authorization") token: String,
        @Body application: Application
    ): Response<Application>
}
