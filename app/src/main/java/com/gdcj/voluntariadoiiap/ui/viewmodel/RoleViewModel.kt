package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Role
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RoleListState {
    object Idle : RoleListState()
    object Loading : RoleListState()
    data class Success(val roles: List<Role>) : RoleListState()
    data class Error(val message: String) : RoleListState()
}

sealed class RoleDetailState {
    object Idle : RoleDetailState()
    object Loading : RoleDetailState()
    data class Success(val role: Role) : RoleDetailState()
    data class Error(val message: String) : RoleDetailState()
}

class RoleViewModel : ViewModel() {
    private val _roleListState = MutableStateFlow<RoleListState>(RoleListState.Idle)
    val roleListState = _roleListState.asStateFlow()

    private val _roleDetailState = MutableStateFlow<RoleDetailState>(RoleDetailState.Idle)
    val roleDetailState = _roleDetailState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchRoles() {
        viewModelScope.launch {
            _roleListState.value = RoleListState.Loading
            try {
                val response = RetrofitClient.roleService.getRoles()
                if (response.isSuccessful) {
                    _roleListState.value = RoleListState.Success(response.body() ?: emptyList())
                } else {
                    _roleListState.value = RoleListState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _roleListState.value = RoleListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun fetchRoleById(id: Int) {
        viewModelScope.launch {
            _roleDetailState.value = RoleDetailState.Loading
            try {
                val response = RetrofitClient.roleService.getRoleById(id)
                if (response.isSuccessful && response.body() != null) {
                    _roleDetailState.value = RoleDetailState.Success(response.body()!!)
                } else {
                    _roleDetailState.value = RoleDetailState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _roleDetailState.value = RoleDetailState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createRole(name: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val role = Role(name = name)
                val response = RetrofitClient.roleService.createRole(role)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Rol creado")
                    fetchRoles()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateRole(id: Int, name: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val role = Role(name = name)
                val response = RetrofitClient.roleService.updateRole(id, role)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Rol actualizado")
                    fetchRoles()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteRole(id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val response = RetrofitClient.roleService.deleteRole(id)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Rol eliminado")
                    fetchRoles()
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
