package com.gdcj.voluntariadoiiap.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int? = null,
    val auth0Id: String,
    val name: String,
    val email: String,
    val role_id: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
