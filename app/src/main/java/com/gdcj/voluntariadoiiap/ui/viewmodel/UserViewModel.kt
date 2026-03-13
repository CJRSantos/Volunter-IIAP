package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Experience
import com.gdcj.voluntariadoiiap.data.model.Study
import com.gdcj.voluntariadoiiap.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class UserListState {
    object Idle : UserListState()
    object Loading : UserListState()
    data class Success(val users: List<User>) : UserListState()
    data class Error(val message: String) : UserListState()
}

sealed class UserDetailState {
    object Idle : UserDetailState()
    object Loading : UserDetailState()
    data class Success(val user: User) : UserDetailState()
    data class Error(val message: String) : UserDetailState()
}

sealed class OperationState {
    object Idle : OperationState()
    object Loading : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val message: String) : OperationState()
}

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _userListState = MutableStateFlow<UserListState>(UserListState.Idle)
    val userListState = _userListState.asStateFlow()

    private val _userDetailState = MutableStateFlow<UserDetailState>(UserDetailState.Idle)
    val userDetailState = _userDetailState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    private val _userStudies = MutableStateFlow<List<Study>>(emptyList())
    val userStudies = _userStudies.asStateFlow()

    private val _userExperiences = MutableStateFlow<List<Experience>>(emptyList())
    val userExperiences = _userExperiences.asStateFlow()

    fun fetchUserById(userId: String) {
        viewModelScope.launch {
            _userDetailState.value = UserDetailState.Loading
            try {
                val document = db.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        _userDetailState.value = UserDetailState.Success(user)
                    }
                } else {
                    _userDetailState.value = UserDetailState.Error("Usuario no encontrado")
                }
            } catch (e: Exception) {
                _userDetailState.value = UserDetailState.Error(e.message ?: "Error al cargar perfil")
            }
        }
    }

    fun updateUserInFirebase(userId: String, user: User) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                db.collection("users").document(userId).set(user).await()
                _operationState.value = OperationState.Success("Perfil actualizado")
                fetchUserById(userId)
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                db.collection("users").document(userId).delete().await()
                _operationState.value = OperationState.Success("Usuario eliminado")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error al eliminar")
            }
        }
    }

    fun fetchUserStudies(userId: String) { }
    fun fetchUserExperiences(userId: String) { }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
