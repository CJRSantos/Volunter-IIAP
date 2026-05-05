package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Area
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
            val mockAreas = listOf(
                Area(id = 1, description = "Acuicultura y Manejo Pesquero"),
                Area(id = 2, description = "Biodiversidad Terrestre"),
                Area(id = 3, description = "Cambio Climático y Geoprocesamiento"),
                Area(id = 4, description = "Sociedades Amazónicas"),
                Area(id = 5, description = "Ecosistemas Acuáticos"),
                Area(id = 6, description = "Biotecnología y Bioeconomía"),
                Area(id = 7, description = "Recursos Forestales")
            )
            _areaListState.value = AreaListState.Success(mockAreas)
        }
    }

    fun createArea(name: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            _operationState.value = OperationState.Success("Área creada localmente")
            fetchAreas()
        }
    }

    fun updateArea(id: Int, name: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            _operationState.value = OperationState.Success("Área actualizada localmente")
            fetchAreas()
        }
    }

    fun deleteArea(id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            _operationState.value = OperationState.Success("Área eliminada localmente")
            fetchAreas()
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
