package com.gdcj.voluntariadoiiap.data.model

data class User(
    val id: Int? = null,
    val auth0Id: String? = null,
    val name: String,
    val email: String,
    val role_id: Int? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val location: String? = null,
    val bio: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
