package com.gdcj.voluntariadoiiap.data.model

// Login
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String?,
    val message: String?
)

// Register - Actualizado para incluir campos de la UI
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phone: String? = null,
    val role_id: Int = 1 // Rol por defecto (ej. Voluntario)
)

data class RegisterResponse(
    val message: String?
)

// Logout
data class LogoutResponse(
    val message: String,
    val logoutUrl: String
)

// Change Password
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)

data class ChangePasswordResponse(
    val message: String
)
