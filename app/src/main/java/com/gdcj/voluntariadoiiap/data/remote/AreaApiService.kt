package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Area
import retrofit2.Response
import retrofit2.http.*

interface AreaApiService {

    @GET("areas")
    suspend fun getAreas(): Response<List<Area>>

    @GET("areas/{id}")
    suspend fun getAreaById(
        @Path("id") id: Int
    ): Response<Area>

    @POST("areas")
    suspend fun createArea(
        @Header("Authorization") token: String,
        @Body area: Area
    ): Response<Area>

    @PUT("areas/{id}")
    suspend fun updateArea(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body area: Area
    ): Response<Area>

    @DELETE("areas/{id}")
    suspend fun deleteArea(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}
