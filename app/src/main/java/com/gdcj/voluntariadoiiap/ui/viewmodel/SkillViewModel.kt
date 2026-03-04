package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Skill
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SkillListState {
    object Idle : SkillListState()
    object Loading : SkillListState()
    data class Success(val skills: List<Skill>) : SkillListState()
    data class Error(val message: String) : SkillListState()
}

class SkillViewModel : ViewModel() {
    private val _skillListState = MutableStateFlow<SkillListState>(SkillListState.Idle)
    val skillListState = _skillListState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchSkills() {
        viewModelScope.launch {
            _skillListState.value = SkillListState.Loading
            try {
                val response = RetrofitClient.skillService.getSkills()
                if (response.isSuccessful) {
                    _skillListState.value = SkillListState.Success(response.body() ?: emptyList())
                } else {
                    _skillListState.value = SkillListState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _skillListState.value = SkillListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createSkill(token: String, skill: Skill) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.skillService.createSkill(authToken, skill)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Habilidad creada correctamente")
                    fetchSkills()
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
