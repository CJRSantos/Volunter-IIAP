package com.gdcj.voluntariadoiiap.data.model

import com.google.gson.annotations.SerializedName

data class Experience(
    val id: String? = null,
    val company: String = "",
    val position: String = "",
    val description: String? = null,
    @SerializedName("start_date")
    val startDate: String = "",
    @SerializedName("end_date")
    val endDate: String? = null,
    @SerializedName("user_id")
    val user_id: Int = 0,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    val userUid: String = ""
)
