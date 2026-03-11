package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.gdcj.voluntariadoiiap.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(sessionManager.isDarkModeEnabled())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        sessionManager.saveDarkMode(newValue)
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        sessionManager.saveDarkMode(enabled)
    }
}
