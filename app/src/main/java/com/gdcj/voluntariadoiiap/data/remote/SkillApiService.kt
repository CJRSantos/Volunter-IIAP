package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.model.Skill
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SkillApiService {
    @POST("skills")
    suspend fun createSkill(
        @Header("Authorization") token: String,
        @Body skill: Skill
    ): Response<Skill>
}
