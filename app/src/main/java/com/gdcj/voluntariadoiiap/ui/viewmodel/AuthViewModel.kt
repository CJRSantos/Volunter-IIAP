package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.local.SessionManager
import com.gdcj.voluntariadoiiap.data.model.LoginRequest
import com.gdcj.voluntariadoiiap.data.model.RegisterRequest
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(email: String, pass: String, onSuccess: (String, String) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token ?: ""
                    sessionManager.saveAuthToken(token)
                    
                    _authState.value = AuthState.Success("Bienvenido")
                    onSuccess("Usuario IIAP", email)
                } else {
                    val errorMsg = parseError(response.errorBody()?.string())
                    _authState.value = AuthState.Error(errorMsg ?: "Credenciales inválidas")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun register(name: String, email: String, pass: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Enviamos todos los campos que la UI captura
                val response = RetrofitClient.authService.register(
                    RegisterRequest(
                        email = email, 
                        password = pass, 
                        name = name,
                        phone = phone,
                        role_id = 1 // Por defecto rol de voluntario
                    )
                )
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success("Registro exitoso")
                    onSuccess()
                } else {
                    val errorMsg = parseError(response.errorBody()?.string())
                    _authState.value = AuthState.Error(errorMsg ?: "Error al registrar: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de red: ${e.message}")
            }
        }
    }

    private fun parseError(errorBody: String?): String? {
        if (errorBody == null) return null
        return try {
            val json = JSONObject(errorBody)
            if (json.has("message")) {
                json.getString("message")
            } else if (json.has("error")) {
                json.getString("error")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitClient.authService.logout()
            } catch (e: Exception) {
            } finally {
                sessionManager.clearSession()
                onSuccess()
            }
        }
    }
    
    fun isUserLoggedIn(): Boolean {
        return sessionManager.fetchAuthToken() != null
    }
}
