package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Experience
import retrofit2.Response
import retrofit2.http.*

interface ExperienceApiService {

    @GET("experiences")
    suspend fun getExperiences(): Response<List<Experience>>

    @GET("experiences/{id}")
    suspend fun getExperienceById(
        @Path("id") id: Int
    ): Response<Experience>

    @POST("experiences")
    suspend fun createExperience(
        @Body experience: Experience
    ): Response<Experience>

    @PUT("experiences/{id}")
    suspend fun updateExperience(
        @Path("id") id: Int,
        @Body experience: Experience
    ): Response<Experience>

    @DELETE("experiences/{id}")
    suspend fun deleteExperience(
        @Path("id") id: Int
    ): Response<Unit>
}
