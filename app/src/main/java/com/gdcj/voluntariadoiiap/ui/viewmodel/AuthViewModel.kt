package com.gdcj.voluntariadoiiap.ui.viewmodel

import android.net.Uri
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

class AuthViewModel(val sessionManager: SessionManager) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _userName = MutableStateFlow(sessionManager.fetchUserName() ?: "Usuario IIAP")
    val userName = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(sessionManager.fetchUserEmail() ?: "")
    val userEmail = _userEmail.asStateFlow()

    private val _userId = MutableStateFlow(sessionManager.fetchUserId())
    val userId = _userId.asStateFlow()

    // Estado global para la foto de perfil
    private val _profilePictureUri = MutableStateFlow<Uri?>(null)
    val profilePictureUri = _profilePictureUri.asStateFlow()

    init {
        // Sincronizar datos si ya hay sesión activa
        if (isUserLoggedIn()) {
            val token = sessionManager.fetchAuthToken()
            val email = sessionManager.fetchUserEmail()
            if (token != null && email != null) {
                viewModelScope.launch {
                    fetchAndSaveUserInfo(token, email)
                }
            }
        }
    }

    fun updateProfilePicture(uri: Uri?) {
        _profilePictureUri.value = uri
    }

    fun updateLocalUserData(name: String, email: String) {
        _userName.value = name
        _userEmail.value = email
        sessionManager.saveUserData(name, email)
    }

    fun login(email: String, pass: String, onSuccess: (String, String) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token ?: ""
                    sessionManager.saveAuthToken(token)
                    
                    _userEmail.value = email
                    sessionManager.saveUserData(_userName.value, email)
                    
                    // Obtener info completa del usuario
                    fetchAndSaveUserInfo(token, email)
                    
                    _authState.value = AuthState.Success("Bienvenido")
                    onSuccess(_userName.value, email)
                } else {
                    val errorMsg = parseError(response.errorBody()?.string())
                    _authState.value = AuthState.Error(errorMsg ?: "Credenciales inválidas")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private suspend fun fetchAndSaveUserInfo(token: String, email: String) {
        try {
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val usersResponse = RetrofitClient.userService.getUsers(authToken)
            if (usersResponse.isSuccessful) {
                val user = usersResponse.body()?.find { it.email == email }
                if (user != null) {
                    user.id?.let { 
                        _userId.value = it
                        sessionManager.saveUserId(it)
                    }
                    _userName.value = user.name
                    sessionManager.saveUserData(user.name, email)
                }
            }
        } catch (e: Exception) { }
    }

    fun register(name: String, email: String, pass: String, phone: String, onSuccess: (String, String) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.authService.register(
                    RegisterRequest(
                        email = email, 
                        password = pass, 
                        name = name,
                        phone = phone,
                        role_id = 1
                    )
                )
                if (response.isSuccessful) {
                    // Después de registrar, hacemos login automático para obtener el token
                    login(email, pass, onSuccess)
                } else {
                    val errorMsg = parseError(response.errorBody()?.string())
                    _authState.value = AuthState.Error(errorMsg ?: "Error al registrar")
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
            if (json.has("message")) json.getString("message")
            else if (json.has("error")) json.getString("error")
            else null
        } catch (e: Exception) { null }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            _userName.value = "Usuario IIAP"
            _userEmail.value = ""
            _userId.value = -1
            onSuccess()
        }
    }
    
    fun isUserLoggedIn(): Boolean = sessionManager.fetchAuthToken() != null
}
