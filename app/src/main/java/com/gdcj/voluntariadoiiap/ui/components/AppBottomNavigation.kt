package com.gdcj.voluntariadoiiap.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gdcj.voluntariadoiiap.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigationItems = listOf(
        AppScreens.HomeScreen,
        AppScreens.AreasScreen,
        AppScreens.ConvocatoriasScreen,
        AppScreens.NosotrosScreen
    )

    val showBottomBar = navigationItems.any { it.route == currentDestination?.route || currentDestination?.route?.startsWith(it.route.split("?")[0]) == true }

    if (showBottomBar) {
        // Quitamos el ripple para una transición más limpia
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background, // Usamos background para integración total
                tonalElevation = 0.dp, // Sin elevación para evitar cambios de tono
                windowInsets = WindowInsets.navigationBars // Cubre el área de la barra de gestos del sistema
            ) {
                navigationItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { 
                        it.route?.split("?")?.get(0) == screen.route.split("?")?.get(0) 
                    } == true

                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = screen.icon, 
                                contentDescription = screen.title
                            ) 
                        },
                        label = { Text(screen.title) },
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary, // BLANCO sobre verde
                            selectedTextColor = MaterialTheme.colorScheme.primary,   // TEXTO VERDE
                            indicatorColor = MaterialTheme.colorScheme.primary,       // PILL VERDE
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}
