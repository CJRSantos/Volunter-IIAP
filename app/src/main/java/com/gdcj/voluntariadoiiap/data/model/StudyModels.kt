package com.gdcj.voluntariadoiiap.data.model

import com.google.gson.annotations.SerializedName

data class Study(
    val id: String? = null, // Cambiado a String para IDs de Firestore
    val institution: String = "",
    val degree: String = "",
    @SerializedName("field_of_study")
    val fieldOfStudy: String = "",
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
    val userUid: String = "" // Para vincular con Firebase Auth
)
