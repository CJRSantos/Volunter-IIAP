package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Experience
import com.gdcj.voluntariadoiiap.data.model.Study
import com.gdcj.voluntariadoiiap.data.model.User
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun fetchUsers() {
        viewModelScope.launch {
            _userListState.value = UserListState.Loading
            try {
                val response = RetrofitClient.userService.getUsers()
                if (response.isSuccessful) {
                    _userListState.value = UserListState.Success(response.body() ?: emptyList())
                } else {
                    _userListState.value = UserListState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _userListState.value = UserListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun fetchUserById(id: Int) {
        viewModelScope.launch {
            _userDetailState.value = UserDetailState.Loading
            try {
                val response = RetrofitClient.userService.getUserById(id)
                if (response.isSuccessful && response.body() != null) {
                    _userDetailState.value = UserDetailState.Success(response.body()!!)
                    fetchUserStudies(id)
                    fetchUserExperiences(id)
                } else {
                    _userDetailState.value = UserDetailState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _userDetailState.value = UserDetailState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun fetchUserStudies(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService.getUserStudies(userId)
                if (response.isSuccessful) {
                    _userStudies.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { }
        }
    }

    fun fetchUserExperiences(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService.getUserExperiences(userId)
                if (response.isSuccessful) {
                    _userExperiences.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { }
        }
    }

    fun createUser(user: User) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val response = RetrofitClient.userService.createUser(user)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Usuario creado")
                    fetchUsers()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateUser(id: Int, user: User) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val response = RetrofitClient.userService.updateUser(id, user)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Usuario actualizado")
                    _userDetailState.value = UserDetailState.Success(response.body()!!)
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val response = RetrofitClient.userService.deleteUser(id)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Usuario eliminado")
                    fetchUsers()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }
    
    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
