package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.ApplicationStatus
import retrofit2.Response
import retrofit2.http.GET

interface ApplicationStatusApiService {
    @GET("application_status")
    suspend fun getApplicationStatuses(): Response<List<ApplicationStatus>>
}
