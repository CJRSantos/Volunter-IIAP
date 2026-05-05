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
            // Simulación local
            val user = User(name = "Voluntario IIAP", email = "voluntario@iiap.gob.pe", phone = "987654321")
            _userDetailState.value = UserDetailState.Success(user)
        }
    }

    fun updateUserInFirebase(userId: String, user: User) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            _operationState.value = OperationState.Success("Perfil actualizado localmente")
            _userDetailState.value = UserDetailState.Success(user)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            _operationState.value = OperationState.Success("Usuario eliminado localmente")
        }
    }

    fun fetchUserStudies(userId: String) { }
    fun fetchUserExperiences(userId: String) { }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
