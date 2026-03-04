package com.gdcj.voluntariadoiiap.data.model

data class Study(
    val id: Int? = null,
    val institution: String,
    val degree: String,
    val fieldOfStudy: String,
    val startDate: String,
    val endDate: String? = null,
    val user_id: Int,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
