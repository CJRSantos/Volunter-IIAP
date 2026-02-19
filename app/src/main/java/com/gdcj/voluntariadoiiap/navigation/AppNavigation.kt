package com.gdcj.voluntariadoiiap.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gdcj.voluntariadoiiap.ui.screens.HomeScreen
import com.gdcj.voluntariadoiiap.ui.screens.LoginScreen
import com.gdcj.voluntariadoiiap.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(
                onLoginClick = { 
                    // Por ahora, como ejemplo, pasamos datos genéricos al loguear
                    navController.navigate(AppScreens.HomeScreen.createRoute("Usuario Login", "usuario@gmail.com"))
                },
                onRegisterClick = { navController.navigate(AppScreens.RegisterScreen.route) }
            )
        }
        composable(route = AppScreens.RegisterScreen.route) {
            RegisterScreen(
                onRegisterClick = { name, email ->
                    navController.navigate(AppScreens.HomeScreen.createRoute(name, email))
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(
            route = AppScreens.HomeScreen.route,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            HomeScreen(name = name, email = email)
        }
    }
}
