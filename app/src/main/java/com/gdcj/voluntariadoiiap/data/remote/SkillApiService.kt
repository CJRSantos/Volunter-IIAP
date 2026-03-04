package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Skill
import retrofit2.Response

interface SkillApiService {

    @GET("skills")
    suspend fun getSkills(): Response<List<Skill>>

    @GET("skills/{id}")
    suspend fun getSkillById(
        @Path("id") id: Int
    ): Response<Skill>

    @POST("skills")
    suspend fun createSkill(
        @Header("Authorization") token: String,
        @Body skill: Skill
    ): Response<Skill>

    @PUT("skills/{id}")
    suspend fun updateSkill(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body skill: Skill
    ): Response<Skill>

    @DELETE("skills/{id}")
    suspend fun deleteSkill(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}
