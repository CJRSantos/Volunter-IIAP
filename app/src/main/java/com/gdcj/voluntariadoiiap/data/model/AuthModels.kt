package com.gdcj.voluntariadoiiap.data.model

import com.google.gson.annotations.SerializedName

// Login
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String?, // Suponiendo que la API devuelve un token aunque no lo mencionaste explícitamente en el 200
    val message: String?
)

// Register
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class RegisterResponse(
    val message: String?
)

// Logout
data class LogoutResponse(
    val message: String,
    val logoutUrl: String
)
