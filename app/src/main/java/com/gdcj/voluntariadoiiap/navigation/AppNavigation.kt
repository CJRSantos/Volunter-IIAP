package com.gdcj.voluntariadoiiap.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gdcj.voluntariadoiiap.ui.components.AppBottomNavigation
import com.gdcj.voluntariadoiiap.ui.screens.AdditionalInfoScreen
import com.gdcj.voluntariadoiiap.ui.screens.AreasScreen
import com.gdcj.voluntariadoiiap.ui.screens.ConvocatoriasScreen
import com.gdcj.voluntariadoiiap.ui.screens.HomeScreen
import com.gdcj.voluntariadoiiap.ui.screens.LoginScreen
import com.gdcj.voluntariadoiiap.ui.screens.NosotrosScreen
import com.gdcj.voluntariadoiiap.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.LoginScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppScreens.LoginScreen.route) {
                LoginScreen(
                    onLoginClick = {
                        navController.navigate(
                            AppScreens.HomeScreen.createRoute(
                                "Usuario Login",
                                "usuario@gmail.com"
                            )
                        )
                    },
                    onRegisterClick = {
                        navController.navigate(AppScreens.RegisterScreen.route)
                    }
                )
            }

            composable(AppScreens.RegisterScreen.route) {
                RegisterScreen(
                    onRegisterClick = { name, email ->
                        navController.navigate(
                            AppScreens.HomeScreen.createRoute(name, email)
                        )
                    },
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            composable(
                route = AppScreens.HomeScreen.route,
                arguments = listOf(
                    navArgument("name") { defaultValue = "" },
                    navArgument("email") { defaultValue = "" }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val email = backStackEntry.arguments?.getString("email") ?: ""
                HomeScreen(
                    name = name,
                    email = email,
                    onLogoutNavigate = { navController.navigate(AppScreens.LoginScreen.route) },
                    onNavigateToInfo = { navController.navigate(AppScreens.AdditionalInfoScreen.route) }
                )
            }

            composable(AppScreens.AdditionalInfoScreen.route) {
                AdditionalInfoScreen(onBackClick = { navController.popBackStack() })
            }

            composable(
                route = AppScreens.AreasScreen.route,
                arguments = listOf(
                    navArgument("name") { defaultValue = "" },
                    navArgument("email") { defaultValue = "" }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val email = backStackEntry.arguments?.getString("email") ?: ""
                AreasScreen(
                    name = name,
                    email = email
                )
            }

            composable(route = AppScreens.ConvocatoriasScreen.route) {
                ConvocatoriasScreen()
            }

            composable(
                route = AppScreens.NosotrosScreen.route,
                arguments = listOf(
                    navArgument("name") { defaultValue = "" },
                    navArgument("email") { defaultValue = "" }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val email = backStackEntry.arguments?.getString("email") ?: ""
                NosotrosScreen(
                    name = name,
                    email = email
                )
            }
        }
    }
}
