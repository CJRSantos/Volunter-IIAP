package com.gdcj.voluntariadoiiap.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(val sessionManager: SessionManager) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _userName = MutableStateFlow(sessionManager.fetchUserName() ?: "Voluntario IIAP")
    val userName = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(sessionManager.fetchUserEmail() ?: "voluntario@iiap.gob.pe")
    val userEmail = _userEmail.asStateFlow()

    private val _userId = MutableStateFlow(sessionManager.fetchUserId())
    val userId = _userId.asStateFlow()

    private val _userUid = MutableStateFlow("local_user_uid")
    val userUid = _userUid.asStateFlow()

    private val _profilePictureUri = MutableStateFlow<Uri?>(
        sessionManager.fetchProfilePicture()?.let { Uri.parse(it) }
    )
    val profilePictureUri = _profilePictureUri.asStateFlow()

    fun updateProfilePicture(uri: Uri?) {
        _profilePictureUri.value = uri
        sessionManager.saveProfilePicture(uri?.toString())
    }

    fun updateLocalUserData(name: String, email: String) {
        _userName.value = name
        _userEmail.value = email
        sessionManager.saveUserData(name, email)
    }

    fun login(email: String, pass: String, onSuccess: (String, String) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Simulación local exitosa sin Firebase
            val name = "Voluntario IIAP"
            sessionManager.saveAuthToken("local_token")
            _userName.value = name
            _userEmail.value = email
            _userUid.value = "local_user_uid"
            
            val dummyId = 12345
            _userId.value = dummyId
            sessionManager.saveUserId(dummyId)
            sessionManager.saveUserData(name, email)
            
            _authState.value = AuthState.Success("Bienvenido (Modo Local)")
            onSuccess(name, email)
        }
    }

    fun register(name: String, email: String, pass: String, phone: String, onSuccess: (String, String) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Simulación local exitosa sin Firebase
            sessionManager.saveAuthToken("local_token")
            _userName.value = name
            _userEmail.value = email
            _userUid.value = "local_user_uid"
            
            val dummyId = 12345
            _userId.value = dummyId
            sessionManager.saveUserId(dummyId)
            sessionManager.saveUserData(name, email)
            
            _authState.value = AuthState.Success("Registro exitoso (Modo Local)")
            onSuccess(name, email)
        }
    }

    fun changePassword(current: String, new: String, confirm: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _authState.value = AuthState.Success("Contraseña actualizada localmente")
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            _userName.value = "Voluntario IIAP"
            _userEmail.value = "voluntario@iiap.gob.pe"
            _userId.value = -1
            _userUid.value = ""
            _profilePictureUri.value = null
            onSuccess()
        }
    }
    
    fun isUserLoggedIn(): Boolean = true
}
