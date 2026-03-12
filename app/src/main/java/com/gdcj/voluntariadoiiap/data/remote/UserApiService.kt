package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Experience
import com.gdcj.voluntariadoiiap.data.model.Study
import com.gdcj.voluntariadoiiap.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @POST("users")
    suspend fun createUser(
        @Body user: User
    ): Response<User>

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body user: User
    ): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("users/{id}/studies")
    suspend fun getUserStudies(
        @Path("id") userId: Int
    ): Response<List<Study>>

    @GET("users/{id}/experiences")
    suspend fun getUserExperiences(
        @Path("id") userId: Int
    ): Response<List<Experience>>
}
