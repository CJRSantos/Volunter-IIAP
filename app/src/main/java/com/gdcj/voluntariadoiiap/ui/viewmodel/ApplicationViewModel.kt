package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Application
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ApplicationOperationState {
    object Idle : ApplicationOperationState()
    object Loading : ApplicationOperationState()
    data class Success(val message: String) : ApplicationOperationState()
    data class Error(val message: String) : ApplicationOperationState()
}

class ApplicationViewModel : ViewModel() {
    private val _operationState = MutableStateFlow<ApplicationOperationState>(ApplicationOperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun applyToProject(token: String, userId: Int, projectId: Int, motivation: String) {
        viewModelScope.launch {
            _operationState.value = ApplicationOperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                // Asumiendo que status_id 1 es "Pendiente"
                val application = Application(
                    user_id = userId,
                    project_id = projectId,
                    status_id = 1,
                    motivation = motivation
                )
                
                val response = RetrofitClient.applicationService.createApplication(authToken, application)
                
                if (response.isSuccessful) {
                    _operationState.value = ApplicationOperationState.Success("¡Postulación enviada con éxito!")
                } else {
                    _operationState.value = ApplicationOperationState.Error("Error al postular: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = ApplicationOperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _operationState.value = ApplicationOperationState.Idle
    }
}
