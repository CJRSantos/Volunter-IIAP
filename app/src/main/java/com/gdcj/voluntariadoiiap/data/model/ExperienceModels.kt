package com.gdcj.voluntariadoiiap.data.model

data class Experience(
    val id: Int? = null,
    val company: String,
    val position: String,
    val description: String? = null,
    val startDate: String,
    val endDate: String? = null,
    val user_id: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
