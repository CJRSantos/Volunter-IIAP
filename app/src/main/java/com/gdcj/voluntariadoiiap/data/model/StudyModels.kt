package com.gdcj.voluntariadoiiap.data.model

import com.google.gson.annotations.SerializedName

data class Study(
    val id: Int? = null,
    val institution: String,
    val degree: String,
    @SerializedName("field_of_study")
    val fieldOfStudy: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String? = null,
    @SerializedName("user_id")
    val user_id: Int,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
