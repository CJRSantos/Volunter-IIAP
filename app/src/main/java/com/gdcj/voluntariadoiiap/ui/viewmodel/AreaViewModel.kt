package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Area
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AreaListState {
    object Idle : AreaListState()
    object Loading : AreaListState()
    data class Success(val areas: List<Area>) : AreaListState()
    data class Error(val message: String) : AreaListState()
}

class AreaViewModel : ViewModel() {
    private val _areaListState = MutableStateFlow<AreaListState>(AreaListState.Idle)
    val areaListState = _areaListState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchAreas() {
        viewModelScope.launch {
            _areaListState.value = AreaListState.Loading
            try {
                val response = RetrofitClient.areaService.getAreas()
                if (response.isSuccessful) {
                    _areaListState.value = AreaListState.Success(response.body() ?: emptyList())
                } else {
                    _areaListState.value = AreaListState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _areaListState.value = AreaListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createArea(token: String, area: Area) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.areaService.createArea(authToken, area)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Área creada")
                    fetchAreas()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateArea(token: String, id: Int, area: Area) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.areaService.updateArea(authToken, id, area)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Área actualizada")
                    fetchAreas()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteArea(token: String, id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.areaService.deleteArea(authToken, id)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Área eliminada")
                    fetchAreas()
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
