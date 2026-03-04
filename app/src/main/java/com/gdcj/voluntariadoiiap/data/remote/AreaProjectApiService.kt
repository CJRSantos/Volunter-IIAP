package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.AreaProject
import retrofit2.Response
import retrofit2.http.*

interface AreaProjectApiService {

    @GET("areaProjects")
    suspend fun getAreaProjects(): Response<List<AreaProject>>

    @GET("areaProjects/{id}")
    suspend fun getAreaProjectById(
        @Path("id") id: Int
    ): Response<AreaProject>

    @POST("areaProjects")
    suspend fun createAreaProject(
        @Header("Authorization") token: String,
        @Body areaProject: AreaProject
    ): Response<AreaProject>

    @PUT("areaProjects/{id}")
    suspend fun updateAreaProject(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body areaProject: AreaProject
    ): Response<AreaProject>

    @DELETE("areaProjects/{id}")
    suspend fun deleteAreaProject(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}
