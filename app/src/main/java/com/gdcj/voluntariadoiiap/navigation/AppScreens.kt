package com.gdcj.voluntariadoiiap.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreens(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object LoginScreen : AppScreens("login_screen", "Login", Icons.Default.Home) // Icon is not used here
    object RegisterScreen : AppScreens("register_screen", "Register", Icons.Default.Home) // Icon is not used here

    object HomeScreen : AppScreens("home_screen", "Inicio", Icons.Default.Home) {
        fun createRoute(name: String, email: String) = "home_screen?name=$name&email=$email"
    }

    object AreasScreen : AppScreens("areas_screen", "Áreas", Icons.Default.List) {
        fun createRoute(name: String, email: String) = "areas_screen?name=$name&email=$email"
    }

    object ConvocatoriasScreen : AppScreens("convocatorias_screen", "Convocatorias", Icons.AutoMirrored.Filled.CompareArrows) {
        fun createRoute(name: String, email: String) = "convocatorias_screen?name=$name&email=$email"
    }

    object NosotrosScreen : AppScreens("nosotros_screen", "Nosotros", Icons.Default.Groups) {
        fun createRoute(name: String, email: String) = "nosotros_screen?name=$name&email=$email"
    }
}
