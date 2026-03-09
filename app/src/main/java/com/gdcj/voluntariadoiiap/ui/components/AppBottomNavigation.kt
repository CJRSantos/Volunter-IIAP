package com.gdcj.voluntariadoiiap.ui.components

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

    // Hide bottom bar on Login and Register
    val showBottomBar = navigationItems.any { it.route == currentDestination?.route || currentDestination?.route?.startsWith(it.route.split("?")[0]) == true }

    if (showBottomBar) {
        // Desactivamos el ripple (sombra al presionar) para una respuesta visual limpia e inmediata
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                navigationItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { 
                        it.route?.split("?")?.get(0) == screen.route.split("?")?.get(0) 
                    } == true

                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
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
                            selectedIconColor = Color(0xFF2E7D32),
                            selectedTextColor = Color(0xFF2E7D32),
                            indicatorColor = Color(0xFFC8E6C9), // Resalto verde claro
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}
