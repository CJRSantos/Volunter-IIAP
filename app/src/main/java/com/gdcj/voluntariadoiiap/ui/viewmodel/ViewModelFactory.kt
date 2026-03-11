package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gdcj.voluntariadoiiap.data.local.SessionManager

class ViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(sessionManager) as T
            modelClass.isAssignableFrom(ThemeViewModel::class.java) -> ThemeViewModel(sessionManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
