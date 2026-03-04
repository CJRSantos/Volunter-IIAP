package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Experience
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ExperienceListState {
    object Idle : ExperienceListState()
    object Loading : ExperienceListState()
    data class Success(val experiences: List<Experience>) : ExperienceListState()
    data class Error(val message: String) : ExperienceListState()
}

sealed class ExperienceDetailState {
    object Idle : ExperienceDetailState()
    object Loading : ExperienceDetailState()
    data class Success(val experience: Experience) : ExperienceDetailState()
    data class Error(val message: String) : ExperienceDetailState()
}

class ExperienceViewModel : ViewModel() {
    private val _experienceListState = MutableStateFlow<ExperienceListState>(ExperienceListState.Idle)
    val experienceListState = _experienceListState.asStateFlow()

    private val _experienceDetailState = MutableStateFlow<ExperienceDetailState>(ExperienceDetailState.Idle)
    val experienceDetailState = _experienceDetailState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchExperiences() {
        viewModelScope.launch {
            _experienceListState.value = ExperienceListState.Loading
            try {
                val response = RetrofitClient.experienceService.getExperiences()
                if (response.isSuccessful) {
                    _experienceListState.value = ExperienceListState.Success(response.body() ?: emptyList())
                } else {
                    _experienceListState.value = ExperienceListState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _experienceListState.value = ExperienceListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun fetchExperienceById(id: Int) {
        viewModelScope.launch {
            _experienceDetailState.value = ExperienceDetailState.Loading
            try {
                val response = RetrofitClient.experienceService.getExperienceById(id)
                if (response.isSuccessful && response.body() != null) {
                    _experienceDetailState.value = ExperienceDetailState.Success(response.body()!!)
                } else {
                    _experienceDetailState.value = ExperienceDetailState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _experienceDetailState.value = ExperienceDetailState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createExperience(token: String, experience: Experience) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.experienceService.createExperience(authToken, experience)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Experiencia creada correctamente")
                    fetchExperiences()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateExperience(token: String, id: Int, experience: Experience) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.experienceService.updateExperience(authToken, id, experience)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Experiencia actualizada correctamente")
                    fetchExperiences()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteExperience(token: String, id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.experienceService.deleteExperience(authToken, id)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Experiencia eliminada correctamente")
                    fetchExperiences()
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
