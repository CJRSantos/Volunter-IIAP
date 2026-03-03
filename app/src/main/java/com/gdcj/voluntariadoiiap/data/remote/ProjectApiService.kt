package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Project
import retrofit2.Response
import retrofit2.http.*

interface ProjectApiService {

    @GET("projects")
    suspend fun getProjects(): Response<List<Project>>

    @GET("projects/{id}")
    suspend fun getProjectById(
        @Path("id") id: Int
    ): Response<Project>

    @POST("projects")
    suspend fun createProject(
        @Header("Authorization") token: String,
        @Body project: Project
    ): Response<Project>

    @PUT("projects/{id}")
    suspend fun updateProject(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body project: Project
    ): Response<Project>

    @DELETE("projects/{id}")
    suspend fun deleteProject(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}
