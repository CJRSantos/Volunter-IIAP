package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    fun onLogoutClick() {
        _showLogoutDialog.value = true
    }

    fun onDismissLogoutDialog() {
        _showLogoutDialog.value = false
    }

    fun onConfirmLogout() {
        _showLogoutDialog.value = false
        // La lógica de navegación se maneja en la UI (HomeScreen o AppNavigation)
    }
}
