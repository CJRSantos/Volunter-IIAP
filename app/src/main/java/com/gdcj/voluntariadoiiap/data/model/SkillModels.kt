package com.gdcj.voluntariadoiiap.data.model

data class Skill(
    val id: Int? = null,
    val title: String,
    val description: String,
    val yearsOfExperience: Int,
    val user_id: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
