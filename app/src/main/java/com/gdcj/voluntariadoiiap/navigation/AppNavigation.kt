package com.gdcj.voluntariadoiiap.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    
    var showMenuOverlay by remember { mutableStateOf(false) }
    var showProfileOverlay by remember { mutableStateOf(false) }
    var showSecurityOverlay by remember { mutableStateOf(false) }

    val studyViewModel: StudyViewModel = viewModel()
    val experienceViewModel: ExperienceViewModel = viewModel()
    val applicationViewModel: ApplicationViewModel = viewModel()

    val hideHeaderRoutes = listOf(
        AppScreens.LoginScreen.route,
        AppScreens.RegisterScreen.route,
        AppScreens.AreasScreen.route,
        AppScreens.ConvocatoriasScreen.route,
        AppScreens.NosotrosScreen.route,
        AppScreens.PostulacionScreen.route,
        AppScreens.AdditionalInfoScreen.route
    )
    
    val showHeader = currentRoute != null && hideHeaderRoutes.none { route -> 
        currentRoute.startsWith(route.split("?")[0]) 
    }

    val name by authViewModel.userName.collectAsState()
    val email by authViewModel.userEmail.collectAsState()

    val startDestination = AppScreens.HomeScreen.route

    BackHandler(enabled = showMenuOverlay || showProfileOverlay || showSecurityOverlay) {
        if (showSecurityOverlay) {
            showSecurityOverlay = false
            showMenuOverlay = true
        } else if (showProfileOverlay) {
            showProfileOverlay = false
            showMenuOverlay = true
        } else {
            showMenuOverlay = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Scaffold(
            topBar = {
                if (showHeader) {
                    UserHeader(
                        name = name,
                        email = email,
                        authViewModel = authViewModel,
                        onMenuClick = { showMenuOverlay = true }
                    )
                }
            },
            bottomBar = { AppBottomNavigation(navController = navController) },
            containerColor = Color.Transparent
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AppScreens.LoginScreen.route) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginClick = { n, e ->
                            navController.navigate(AppScreens.HomeScreen.route) {
                                popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                            }
                        },
                        onRegisterClick = { navController.navigate(AppScreens.RegisterScreen.route) }
                    )
                }
                composable(AppScreens.RegisterScreen.route) {
                    RegisterScreen(
                        authViewModel = authViewModel,
                        onRegisterClick = { n, e ->
                            navController.navigate(AppScreens.HomeScreen.route) {
                                popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                            }
                        },
                        onBackToLogin = { navController.popBackStack() }
                    )
                }

                composable(route = AppScreens.HomeScreen.route) {
                    HomeScreen(
                        name = name, 
                        email = email, 
                        themeViewModel = themeViewModel, 
                        authViewModel = authViewModel,
                        onLogoutNavigate = { navController.navigate(AppScreens.LoginScreen.route) { popUpTo(0) { inclusive = true } } },
                        onNavigateToInfo = { navController.navigate(AppScreens.AdditionalInfoScreen.route) },
                        onNavigateToAreas = { navController.navigate(AppScreens.AreasScreen.route) },
                        onProfileClick = { showProfileOverlay = true }
                    )
                }

                composable(route = AppScreens.AreasScreen.route) {
                    AreasScreen(
                        areaViewModel = areaViewModel, 
                        name = name, 
                        email = email,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(route = AppScreens.ConvocatoriasScreen.route) {
                    ConvocatoriasScreen(
                        projectViewModel = projectViewModel, 
                        authViewModel = authViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(route = AppScreens.NosotrosScreen.route) {
                    NosotrosScreen(name = name, email = email)
                }
                
                composable(AppScreens.AdditionalInfoScreen.route) { 
                    AdditionalInfoScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToPostulacion = { navController.navigate(AppScreens.PostulacionScreen.route) }
                    ) 
                }

                composable(AppScreens.PostulacionScreen.route) {
                    PostulacionScreen(
                        authViewModel = authViewModel,
                        applicationViewModel = applicationViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }

        if (showHeader) {
            AnimatedVisibility(visible = showMenuOverlay || showProfileOverlay || showSecurityOverlay, enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { 
                    showMenuOverlay = false
                    showProfileOverlay = false
                    showSecurityOverlay = false
                })
            }

            AnimatedVisibility(
                visible = showMenuOverlay,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd).systemBarsPadding()
            ) {
                AppDrawerContent(
                    name = name, 
                    email = email, 
                    authViewModel = authViewModel,
                    themeViewModel = themeViewModel,
                    onProfileClick = { showMenuOverlay = false; showProfileOverlay = true },
                    onSecurityClick = { showMenuOverlay = false; showSecurityOverlay = true },
                    onLogoutClick = {
                        showMenuOverlay = false
                        authViewModel.logout { navController.navigate(AppScreens.LoginScreen.route) { popUpTo(0) { inclusive = true } } }
                    }
                )
            }

            AnimatedVisibility(
                visible = showProfileOverlay,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.fillMaxSize().systemBarsPadding()
            ) {
                ProfileScreen(
                    name = name, email = email, userViewModel = userViewModel, authViewModel = authViewModel, 
                    studyViewModel = studyViewModel, experienceViewModel = experienceViewModel,
                    onBackClick = { 
                        showProfileOverlay = false
                        showMenuOverlay = true
                    }
                )
            }

            AnimatedVisibility(
                visible = showSecurityOverlay,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.fillMaxSize().systemBarsPadding()
            ) {
                SecurityScreen(
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    onBackClick = { 
                        showSecurityOverlay = false
                        showMenuOverlay = true
                    },
                    onAccountDeleted = {
                        showSecurityOverlay = false
                        navController.navigate(AppScreens.LoginScreen.route) { popUpTo(0) { inclusive = true } }
                    }
                )
            }
        }
    }
}
