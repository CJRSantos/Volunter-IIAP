package com.gdcj.voluntariadoiiap.data.model

data class Application(
    val id: Int? = null,
    val user_id: Int,
    val project_id: Int,
    val status_id: Int,
    val motivation: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
