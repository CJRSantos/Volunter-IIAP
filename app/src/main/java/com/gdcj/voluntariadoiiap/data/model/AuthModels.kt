package com.gdcj.voluntariadoiiap.data.model

import com.google.gson.annotations.SerializedName

// Login
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("token", alternate = ["accessToken", "access_token"])
    val token: String?,
    @SerializedName("message", alternate = ["msg", "error"])
    val message: String?
)

// Register
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String? = null,
    val role_id: Int = 1
)

data class RegisterResponse(
    val message: String?
)

// Logout
data class LogoutResponse(
    val message: String,
    val logoutUrl: String
)
