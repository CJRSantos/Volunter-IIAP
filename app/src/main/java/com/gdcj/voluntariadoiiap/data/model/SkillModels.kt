package com.gdcj.voluntariadoiiap.data.model

data class Skill(
    val id: Int? = null,
    val description: String,
    val title: String,
    val yearsOfExperience: Int,
    val user_id: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
