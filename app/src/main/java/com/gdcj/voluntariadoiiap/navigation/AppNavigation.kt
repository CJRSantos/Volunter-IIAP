package com.gdcj.voluntariadoiiap.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gdcj.voluntariadoiiap.ui.components.AppBottomNavigation
import com.gdcj.voluntariadoiiap.ui.components.AppDrawerContent
import com.gdcj.voluntariadoiiap.ui.components.UserHeader
import com.gdcj.voluntariadoiiap.ui.screens.*
import com.gdcj.voluntariadoiiap.ui.viewmodel.*
import kotlinx.coroutines.launch

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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val studyViewModel: StudyViewModel = viewModel()
    val experienceViewModel: ExperienceViewModel = viewModel()

    val hideHeaderRoutes = listOf(
        AppScreens.LoginScreen.route,
        AppScreens.RegisterScreen.route,
        AppScreens.ProfileScreen.route
    )
    
    val showHeader = currentRoute != null && hideHeaderRoutes.none { route -> 
        currentRoute.startsWith(route.split("?")[0]) 
    }

    val name by authViewModel.userName.collectAsState()
    val email by authViewModel.userEmail.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showHeader,
        drawerContent = {
            if (showHeader) {
                AppDrawerContent(
                    name = name,
                    email = email,
                    authViewModel = authViewModel,
                    onProfileClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppScreens.ProfileScreen.createRoute(name, email))
                    },
                    onLogoutClick = {
                        scope.launch { drawerState.close() }
                        authViewModel.logout {
                            navController.navigate(AppScreens.LoginScreen.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                        // Implementar navegación a configuración si existe
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showHeader) {
                    UserHeader(
                        name = name,
                        email = email,
                        authViewModel = authViewModel,
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            },
            bottomBar = { AppBottomNavigation(navController = navController) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = if (authViewModel.isUserLoggedIn()) AppScreens.HomeScreen.route else AppScreens.LoginScreen.route,
                    modifier = Modifier.padding(
                        top = if (showHeader) innerPadding.calculateTopPadding() else 0.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    )
                ) {
                    composable(AppScreens.LoginScreen.route) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginClick = { nameParam, emailParam ->
                                navController.navigate(
                                    AppScreens.HomeScreen.createRoute(nameParam, emailParam)
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
                            onRegisterClick = { nameParam, emailParam ->
                                navController.navigate(
                                    AppScreens.HomeScreen.createRoute(nameParam, emailParam)
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
                        val nameArg = backStackEntry.arguments?.getString("name") ?: ""
                        val emailArg = backStackEntry.arguments?.getString("email") ?: ""
                        HomeScreen(
                            name = nameArg,
                            email = emailArg,
                            themeViewModel = themeViewModel,
                            authViewModel = authViewModel,
                            onLogoutNavigate = { 
                                navController.navigate(AppScreens.LoginScreen.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onNavigateToInfo = { navController.navigate(AppScreens.AdditionalInfoScreen.route) },
                            onProfileClick = {
                                navController.navigate(AppScreens.ProfileScreen.createRoute(nameArg, emailArg))
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
                        val nameArg = backStackEntry.arguments?.getString("name") ?: ""
                        val emailArg = backStackEntry.arguments?.getString("email") ?: ""
                        ProfileScreen(
                            name = nameArg,
                            email = emailArg,
                            userViewModel = userViewModel,
                            authViewModel = authViewModel,
                            studyViewModel = studyViewModel,
                            experienceViewModel = experienceViewModel,
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
                        val nameArg = backStackEntry.arguments?.getString("name") ?: ""
                        val emailArg = backStackEntry.arguments?.getString("email") ?: ""
                        AreasScreen(
                            areaViewModel = areaViewModel,
                            name = nameArg,
                            email = emailArg
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
                        val nameArg = backStackEntry.arguments?.getString("name") ?: ""
                        val emailArg = backStackEntry.arguments?.getString("email") ?: ""
                        NosotrosScreen(
                            name = nameArg,
                            email = emailArg
                        )
                    }
                }
            }
        }
    }
}
