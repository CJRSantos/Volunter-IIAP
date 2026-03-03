package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchUsers(token: String) {
        viewModelScope.launch {
            _userListState.value = UserListState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.userService.getUsers(authToken)
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

    fun fetchUserById(token: String, id: Int) {
        viewModelScope.launch {
            _userDetailState.value = UserDetailState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.userService.getUserById(authToken, id)
                if (response.isSuccessful && response.body() != null) {
                    _userDetailState.value = UserDetailState.Success(response.body()!!)
                } else {
                    _userDetailState.value = UserDetailState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _userDetailState.value = UserDetailState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createUser(token: String, user: User) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.userService.createUser(authToken, user)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Usuario creado")
                    fetchUsers(token)
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateUser(token: String, id: Int, user: User) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.userService.updateUser(authToken, id, user)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Usuario actualizado")
                    fetchUsers(token)
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteUser(token: String, id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.userService.deleteUser(authToken, id)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Usuario eliminado")
                    fetchUsers(token)
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
