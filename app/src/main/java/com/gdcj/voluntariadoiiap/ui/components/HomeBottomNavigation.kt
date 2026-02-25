package com.gdcj.voluntariadoiiap.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun HomeBottomNavigation(
    onAreasClick: () -> Unit,
    onNosotrosClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {

        NavigationBarItem(
            selected = true,
            onClick = { /* Ya estás en Home */ },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { onAreasClick() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Áreas"
                )
            },
            label = { Text("Áreas") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { onNosotrosClick() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Nosotros"
                )
            },
            label = { Text("Nosotros") }
        )
    }
}