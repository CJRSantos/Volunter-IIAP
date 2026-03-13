package com.gdcj.voluntariadoiiap.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.local.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(val sessionManager: SessionManager) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _userName = MutableStateFlow(sessionManager.fetchUserName() ?: "Usuario IIAP")
    val userName = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(sessionManager.fetchUserEmail() ?: "")
    val userEmail = _userEmail.asStateFlow()

    private val _userId = MutableStateFlow(sessionManager.fetchUserId())
    val userId = _userId.asStateFlow()

    private val _userUid = MutableStateFlow(auth.currentUser?.uid ?: "")
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
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val user = result.user
                
                if (user != null) {
                    val token = user.getIdToken(false).await().token ?: ""
                    sessionManager.saveAuthToken(token)
                    
                    val name = user.displayName ?: "Usuario IIAP"
                    _userName.value = name
                    _userEmail.value = email
                    _userUid.value = user.uid
                    
                    val dummyId = user.uid.hashCode() 
                    _userId.value = dummyId
                    sessionManager.saveUserId(dummyId)
                    sessionManager.saveUserData(name, email)
                    
                    _authState.value = AuthState.Success("Bienvenido")
                    onSuccess(name, email)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun register(name: String, email: String, pass: String, phone: String, onSuccess: (String, String) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val user = result.user
                
                if (user != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user.updateProfile(profileUpdates).await()
                    
                    login(email, pass, onSuccess)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al registrar")
            }
        }
    }

    fun changePassword(current: String, new: String, confirm: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, current)
                    user.reauthenticate(credential).await()
                    user.updatePassword(new).await()
                    _authState.value = AuthState.Success("Contraseña actualizada")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error: Verifica tu contraseña actual")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            auth.signOut()
            sessionManager.clearSession()
            _userName.value = "Usuario IIAP"
            _userEmail.value = ""
            _userId.value = -1
            _userUid.value = ""
            _profilePictureUri.value = null
            onSuccess()
        }
    }
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null || sessionManager.fetchAuthToken() != null
}
