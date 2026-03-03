package com.gdcj.voluntariadoiiap.data.model

data class Project(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val startDate: String,
    val endDate: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
