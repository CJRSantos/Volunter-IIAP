package com.gdcj.voluntariadoiiap.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gdcj.voluntariadoiiap.navigation.AppScreens

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
        NavigationBar(
            containerColor = Color.White
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
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4CAF50),
                        selectedTextColor = Color(0xFF4CAF50),
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
            }
        }
    }
}
