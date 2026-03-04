package com.gdcj.voluntariadoiiap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.gdcj.voluntariadoiiap.ui.viewmodel.ThemeViewModel

@Composable
fun UserHeader(
    name: String,
    email: String,
    themeViewModel: ThemeViewModel,
    onLogoutClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    // Usamos Surface para dar un fondo sólido y manejar elevación si es necesario
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Este modificador evita el solapamiento con la barra de estado
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Text("Bienvenido", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            // Menu Hamburguesa
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }

                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                ) {
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .width(200.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 8.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil", fontSize = 16.sp) },
                            onClick = {
                                menuExpanded = false
                                onProfileClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.PersonOutline, contentDescription = null, modifier = Modifier.size(24.dp))
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        )

                        DropdownMenuItem(
                            text = { Text("Configuración", fontSize = 16.sp) },
                            onClick = {
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(24.dp))
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.height(8.dp))

                        DropdownMenuItem(
                            text = { Text("Cerrar sesión", color = Color(0xFFE57373), fontSize = 16.sp) },
                            onClick = {
                                menuExpanded = false
                                onLogoutClick()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.ExitToApp,
                                    contentDescription = null,
                                    tint = Color(0xFFE57373),
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
