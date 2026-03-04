package com.gdcj.voluntariadoiiap.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gdcj.voluntariadoiiap.ui.components.AppBottomNavigation
import com.gdcj.voluntariadoiiap.ui.components.UserHeader
import com.gdcj.voluntariadoiiap.ui.screens.*
import com.gdcj.voluntariadoiiap.ui.viewmodel.*

@Composable
fun AppNavigation(
    themeViewModel: ThemeViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    roleViewModel: RoleViewModel,
    areaViewModel: AreaViewModel,
    projectViewModel: ProjectViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definir en qué pantallas NO mostrar el encabezado
    val hideHeaderRoutes = listOf(
        AppScreens.LoginScreen.route,
        AppScreens.RegisterScreen.route,
        AppScreens.ProfileScreen.route
    )
    
    val showHeader = currentRoute != null && hideHeaderRoutes.none { route -> 
        currentRoute.startsWith(route.split("?")[0]) 
    }

    Scaffold(
        topBar = {
            if (showHeader) {
                val name by authViewModel.userName.collectAsState()
                val email by authViewModel.userEmail.collectAsState()
                UserHeader(
                    name = name,
                    email = email,
                    themeViewModel = themeViewModel,
                    onLogoutClick = {
                        authViewModel.logout {
                            navController.navigate(AppScreens.LoginScreen.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onProfileClick = {
                        navController.navigate(AppScreens.ProfileScreen.createRoute(name, email))
                    }
                )
            }
        },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authViewModel.isUserLoggedIn()) AppScreens.HomeScreen.route else AppScreens.LoginScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppScreens.LoginScreen.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginClick = { name, email ->
                        navController.navigate(
                            AppScreens.HomeScreen.createRoute(name, email)
                        ) {
                            popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(AppScreens.RegisterScreen.route)
                    }
                )
            }

            composable(AppScreens.RegisterScreen.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterClick = { name, email ->
                        navController.navigate(
                            AppScreens.HomeScreen.createRoute(name, email)
                        ) {
                            popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                        }
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
                    themeViewModel = themeViewModel,
                    authViewModel = authViewModel,
                    onLogoutNavigate = { 
                        navController.navigate(AppScreens.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToInfo = { navController.navigate(AppScreens.AdditionalInfoScreen.route) },
                    onProfileClick = {
                        navController.navigate(AppScreens.ProfileScreen.createRoute(name, email))
                    }
                )
            }

            composable(AppScreens.AdditionalInfoScreen.route) {
                AdditionalInfoScreen(onBackClick = { navController.popBackStack() })
            }

            composable(
                route = AppScreens.ProfileScreen.route,
                arguments = listOf(
                    navArgument("name") { defaultValue = "" },
                    navArgument("email") { defaultValue = "" }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val email = backStackEntry.arguments?.getString("email") ?: ""
                ProfileScreen(
                    name = name,
                    email = email,
                    userViewModel = userViewModel,
                    roleViewModel = roleViewModel,
                    areaViewModel = areaViewModel,
                    projectViewModel = projectViewModel,
                    onBackClick = { navController.popBackStack() }
                )
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
                    areaViewModel = areaViewModel,
                    name = name,
                    email = email
                )
            }

            composable(route = AppScreens.ConvocatoriasScreen.route) {
                ConvocatoriasScreen(
                    projectViewModel = projectViewModel,
                    authViewModel = authViewModel
                )
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
