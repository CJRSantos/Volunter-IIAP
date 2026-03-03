package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Role
import retrofit2.Response
import retrofit2.http.*

interface RoleApiService {

    @GET("roles")
    suspend fun getRoles(): Response<List<Role>>

    @GET("roles/{id}")
    suspend fun getRoleById(
        @Path("id") id: Int
    ): Response<Role>

    @POST("roles")
    suspend fun createRole(
        @Header("Authorization") token: String,
        @Body role: Role
    ): Response<Role>

    @PUT("roles/{id}")
    suspend fun updateRole(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body role: Role
    ): Response<Role>

    @DELETE("roles/{id}")
    suspend fun deleteRole(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}
