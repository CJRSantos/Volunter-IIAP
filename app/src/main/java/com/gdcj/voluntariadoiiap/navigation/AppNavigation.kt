package com.gdcj.voluntariadoiiap.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gdcj.voluntariadoiiap.ui.screens.LoginScreen
import com.gdcj.voluntariadoiiap.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(
                onLoginClick = { /*TODO: Implement login logic*/ },
                onRegisterClick = { navController.navigate(AppScreens.RegisterScreen.route) }
            )
        }
        composable(route = AppScreens.RegisterScreen.route) {
            RegisterScreen(
                onRegisterClick = { /*TODO: Implement registration logic*/ },
                onBackToLogin = { navController.popBackStack() }
            )
        }
    }
}
