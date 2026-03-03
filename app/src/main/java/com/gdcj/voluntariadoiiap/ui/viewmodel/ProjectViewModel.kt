package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Project
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProjectListState {
    object Idle : ProjectListState()
    object Loading : ProjectListState()
    data class Success(val projects: List<Project>) : ProjectListState()
    data class Error(val message: String) : ProjectListState()
}

class ProjectViewModel : ViewModel() {
    private val _projectListState = MutableStateFlow<ProjectListState>(ProjectListState.Idle)
    val projectListState = _projectListState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchProjects() {
        viewModelScope.launch {
            _projectListState.value = ProjectListState.Loading
            try {
                val response = RetrofitClient.projectService.getProjects()
                if (response.isSuccessful) {
                    _projectListState.value = ProjectListState.Success(response.body() ?: emptyList())
                } else {
                    _projectListState.value = ProjectListState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _projectListState.value = ProjectListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createProject(token: String, project: Project) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.projectService.createProject(authToken, project)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Proyecto creado")
                    fetchProjects()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateProject(token: String, id: Int, project: Project) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.projectService.updateProject(authToken, id, project)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Proyecto actualizado")
                    fetchProjects()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteProject(token: String, id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.projectService.deleteProject(authToken, id)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Proyecto eliminado")
                    fetchProjects()
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
