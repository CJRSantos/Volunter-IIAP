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

    val studyViewModel: StudyViewModel = viewModel()
    val experienceViewModel: ExperienceViewModel = viewModel()

    val hideHeaderRoutes = listOf(
        AppScreens.LoginScreen.route,
        AppScreens.RegisterScreen.route
    )
    
    val showHeader = currentRoute != null && hideHeaderRoutes.none { route -> 
        currentRoute.startsWith(route.split("?")[0]) 
    }

    val name by authViewModel.userName.collectAsState()
    val email by authViewModel.userEmail.collectAsState()

    val startDestination = remember {
        if (authViewModel.isUserLoggedIn()) AppScreens.HomeScreen.route else AppScreens.LoginScreen.route
    }

    BackHandler(enabled = showMenuOverlay || showProfileOverlay) {
        if (showProfileOverlay) {
            showProfileOverlay = false
            showMenuOverlay = true
        } else {
            showMenuOverlay = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            bottomBar = { AppBottomNavigation(navController = navController) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(AppScreens.LoginScreen.route) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginClick = { n, e ->
                                navController.navigate(AppScreens.HomeScreen.createRoute(n, e)) {
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
                                navController.navigate(AppScreens.HomeScreen.createRoute(n, e)) {
                                    popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                                }
                            },
                            onBackToLogin = { navController.popBackStack() }
                        )
                    }

                    val mainScreenFade = fadeIn(animationSpec = tween(300))
                    val mainScreenExit = fadeOut(animationSpec = tween(300))

                    composable(
                        route = AppScreens.HomeScreen.route,
                        arguments = listOf(navArgument("name") { defaultValue = "" }, navArgument("email") { defaultValue = "" }),
                        enterTransition = { mainScreenFade }, exitTransition = { mainScreenExit }
                    ) { backStackEntry ->
                        val n = backStackEntry.arguments?.getString("name") ?: ""
                        val e = backStackEntry.arguments?.getString("email") ?: ""
                        HomeScreen(name = n, email = e, themeViewModel = themeViewModel, authViewModel = authViewModel,
                            onLogoutNavigate = { navController.navigate(AppScreens.LoginScreen.route) { popUpTo(0) { inclusive = true } } },
                            onNavigateToInfo = { navController.navigate(AppScreens.AdditionalInfoScreen.route) },
                            onProfileClick = { showProfileOverlay = true }
                        )
                    }

                    composable(route = AppScreens.AreasScreen.route, arguments = listOf(navArgument("name") { defaultValue = "" }, navArgument("email") { defaultValue = "" }),
                        enterTransition = { mainScreenFade }, exitTransition = { mainScreenExit }
                    ) { backStackEntry ->
                        val n = backStackEntry.arguments?.getString("name") ?: ""
                        val e = backStackEntry.arguments?.getString("email") ?: ""
                        AreasScreen(areaViewModel = areaViewModel, name = n, email = e)
                    }

                    composable(route = AppScreens.ConvocatoriasScreen.route, enterTransition = { mainScreenFade }, exitTransition = { mainScreenExit }) {
                        ConvocatoriasScreen(projectViewModel = projectViewModel, authViewModel = authViewModel)
                    }

                    composable(route = AppScreens.NosotrosScreen.route, arguments = listOf(navArgument("name") { defaultValue = "" }, navArgument("email") { defaultValue = "" }),
                        enterTransition = { mainScreenFade }, exitTransition = { mainScreenExit }
                    ) { backStackEntry ->
                        val n = backStackEntry.arguments?.getString("name") ?: ""
                        val e = backStackEntry.arguments?.getString("email") ?: ""
                        NosotrosScreen(name = n, email = e)
                    }
                    
                    composable(AppScreens.AdditionalInfoScreen.route) { AdditionalInfoScreen(onBackClick = { navController.popBackStack() }) }
                }
            }
        }

        // CAPA DE OVERLAYS (MENÚ Y PERFIL)
        if (showHeader) {
            AnimatedVisibility(visible = showMenuOverlay || showProfileOverlay, enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { 
                    showMenuOverlay = false
                    showProfileOverlay = false
                })
            }

            AnimatedVisibility(visible = showMenuOverlay, enter = slideInHorizontally(initialOffsetX = { it }), exit = slideOutHorizontally(targetOffsetX = { it }), modifier = Modifier.align(Alignment.CenterEnd).systemBarsPadding()) {
                AppDrawerContent(
                    name = name, email = email, authViewModel = authViewModel,
                    onProfileClick = { showMenuOverlay = false; showProfileOverlay = true },
                    onLogoutClick = {
                        showMenuOverlay = false
                        authViewModel.logout { navController.navigate(AppScreens.LoginScreen.route) { popUpTo(0) { inclusive = true } } }
                    }
                )
            }

            AnimatedVisibility(visible = showProfileOverlay, enter = slideInHorizontally(initialOffsetX = { it }), exit = slideOutHorizontally(targetOffsetX = { it }), modifier = Modifier.align(Alignment.CenterEnd).systemBarsPadding()) {
                ProfileScreen(
                    name = name, email = email, userViewModel = userViewModel, authViewModel = authViewModel, 
                    studyViewModel = studyViewModel, experienceViewModel = experienceViewModel,
                    onBackClick = { 
                        showProfileOverlay = false
                        showMenuOverlay = true
                    }
                )
            }
        }
    }
}
