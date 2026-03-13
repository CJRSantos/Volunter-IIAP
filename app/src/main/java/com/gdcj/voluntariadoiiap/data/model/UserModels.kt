package com.gdcj.voluntariadoiiap.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int? = null,
    @SerializedName("auth0_id")
    val auth0Id: String? = null,
    val name: String,
    val email: String,
    @SerializedName("role_id")
    val roleId: Int? = null,
    val phone: String? = null,
    @SerializedName("birth_date")
    val birthDate: String? = null,
    val gender: String? = null,
    val location: String? = null,
    val bio: String? = null,
    @SerializedName("volunteer_type")
    val volunteerType: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
