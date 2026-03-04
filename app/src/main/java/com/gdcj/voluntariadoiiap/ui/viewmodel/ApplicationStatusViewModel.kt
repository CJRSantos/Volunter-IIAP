package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.ApplicationStatus
import com.gdcj.voluntariadoiiap.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ApplicationStatusListState {
    object Idle : ApplicationStatusListState()
    object Loading : ApplicationStatusListState()
    data class Success(val statuses: List<ApplicationStatus>) : ApplicationStatusListState()
    data class Error(val message: String) : ApplicationStatusListState()
}

class ApplicationStatusViewModel : ViewModel() {
    private val _statusListState = MutableStateFlow<ApplicationStatusListState>(ApplicationStatusListState.Idle)
    val statusListState = _statusListState.asStateFlow()

    fun fetchApplicationStatuses() {
        viewModelScope.launch {
            _statusListState.value = ApplicationStatusListState.Loading
            try {
                val response = RetrofitClient.applicationStatusService.getApplicationStatuses()
                if (response.isSuccessful) {
                    _statusListState.value = ApplicationStatusListState.Success(response.body() ?: emptyList())
                } else {
                    _statusListState.value = ApplicationStatusListState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _statusListState.value = ApplicationStatusListState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
