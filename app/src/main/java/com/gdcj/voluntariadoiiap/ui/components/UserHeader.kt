package com.gdcj.voluntariadoiiap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun UserHeader(
    name: String,
    email: String,
    onLogoutClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // User info
        Column(modifier = Modifier.weight(1f)) {
            Text("Bienvenido", fontSize = 12.sp, color = Color.Gray)
            Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(email, fontSize = 12.sp, color = Color.Gray)
        }

        // Menu
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Perfil") },
                    onClick = { menuExpanded = false },
                    leadingIcon = {
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Configuración") },
                    onClick = { menuExpanded = false },
                    leadingIcon = {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                )

                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Cerrar sesión", color = Color.Red) },
                    onClick = {
                        menuExpanded = false
                        onLogoutClick()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                )
            }
        }
    }
}