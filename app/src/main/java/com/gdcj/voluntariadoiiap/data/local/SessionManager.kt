package com.gdcj.voluntariadoiiap.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun saveUserId(id: Int) {
        prefs.edit().putInt("user_id", id).apply()
    }

    fun fetchUserId(): Int {
        return prefs.getInt("user_id", -1)
    }

    fun saveUserData(name: String, email: String) {
        prefs.edit().putString("user_name", name).putString("user_email", email).apply()
    }

    fun fetchUserName(): String? = prefs.getString("user_name", null)
    fun fetchUserEmail(): String? = prefs.getString("user_email", null)

    fun saveProfilePicture(uri: String?) {
        prefs.edit().putString("profile_picture", uri).apply()
    }

    fun fetchProfilePicture(): String? {
        return prefs.getString("profile_picture", null)
    }

    // Métodos para el modo oscuro
    fun saveDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    // Biometría
    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean("biometric_enabled", false)
    }

    fun clearSession() {
        val darkMode = isDarkModeEnabled()
        val biometric = isBiometricEnabled()
        prefs.edit().clear().apply()
        saveDarkMode(darkMode)
        setBiometricEnabled(biometric)
    }
}
