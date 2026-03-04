package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Study
import com.gdcj.voluntariadoiiap.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ): Response<List<User>>

    @POST("users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body user: User
    ): Response<User>

    @GET("users/{id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body user: User
    ): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @GET("users/{id}/studies")
    suspend fun getUserStudies(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<List<Study>>
}
