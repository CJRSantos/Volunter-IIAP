package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Study
import retrofit2.Response
import retrofit2.http.*

interface StudyApiService {

    @GET("studies")
    suspend fun getStudies(): Response<List<Study>>

    @GET("studies/{id}")
    suspend fun getStudyById(
        @Path("id") id: Int
    ): Response<Study>

    @POST("studies")
    suspend fun createStudy(
        @Body study: Study
    ): Response<Study>

    @PUT("studies/{id}")
    suspend fun updateStudy(
        @Path("id") id: Int,
        @Body study: Study
    ): Response<Study>

    @DELETE("studies/{id}")
    suspend fun deleteStudy(
        @Path("id") id: Int
    ): Response<Unit>
}
