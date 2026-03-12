package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Study
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StudyListState {
    object Idle : StudyListState()
    object Loading : StudyListState()
    data class Success(val studies: List<Study>) : StudyListState()
    data class Error(val message: String) : StudyListState()
}

sealed class StudyDetailState {
    object Idle : StudyDetailState()
    object Loading : StudyDetailState()
    data class Success(val study: Study) : StudyDetailState()
    data class Error(val message: String) : StudyDetailState()
}

class StudyViewModel : ViewModel() {
    private val _studyListState = MutableStateFlow<StudyListState>(StudyListState.Idle)
    val studyListState = _studyListState.asStateFlow()

    private val _studyDetailState = MutableStateFlow<StudyDetailState>(StudyDetailState.Idle)
    val studyDetailState = _studyDetailState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchStudies() {
        viewModelScope.launch {
            _studyListState.value = StudyListState.Loading
            try {
                val response = RetrofitClient.studyService.getStudies()
                if (response.isSuccessful) {
                    _studyListState.value = StudyListState.Success(response.body() ?: emptyList())
                } else {
                    _studyListState.value = StudyListState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _studyListState.value = StudyListState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun fetchStudyById(id: Int) {
        viewModelScope.launch {
            _studyDetailState.value = StudyDetailState.Loading
            try {
                val response = RetrofitClient.studyService.getStudyById(id)
                if (response.isSuccessful && response.body() != null) {
                    _studyDetailState.value = StudyDetailState.Success(response.body()!!)
                } else {
                    _studyDetailState.value = StudyDetailState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _studyDetailState.value = StudyDetailState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createStudy(study: Study) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val response = RetrofitClient.studyService.createStudy(study)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Estudio registrado correctamente")
                    fetchStudies()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateStudy(id: Int, study: Study) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val response = RetrofitClient.studyService.updateStudy(id, study)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Estudio actualizado correctamente")
                    fetchStudies()
                } else {
                    _operationState.value = OperationState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteStudy(id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val response = RetrofitClient.studyService.deleteStudy(id)
                if (response.isSuccessful) {
                    _operationState.value = OperationState.Success("Estudio eliminado correctamente")
                    fetchStudies()
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
