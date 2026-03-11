package com.gdcj.voluntariadoiiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(onBackClick: () -> Unit) {
    var biometricEnabled by remember { mutableStateOf(false) }
    var twoFactorEnabled by remember { mutableStateOf(false) }
    var publicProfile by remember { mutableStateOf(true) }
    var showDataProcessing by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Seguridad y Privacidad", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                SecuritySectionTitle("Cuenta y Acceso")
                SecurityItem(
                    icon = Icons.Outlined.Lock,
                    title = "Cambiar Contraseña",
                    subtitle = "Actualiza tu contraseña regularmente",
                    onClick = { /* Lógica para cambiar pass */ }
                )
                SecurityItem(
                    icon = Icons.Outlined.Fingerprint,
                    title = "Acceso Biométrico",
                    subtitle = "Usa tu huella para entrar",
                    trailing = {
                        Switch(checked = biometricEnabled, onCheckedChange = { biometricEnabled = it })
                    }
                )
                SecurityItem(
                    icon = Icons.Outlined.VerifiedUser,
                    title = "Autenticación de dos pasos",
                    subtitle = "Añade una capa extra de seguridad",
                    trailing = {
                        Switch(checked = twoFactorEnabled, onCheckedChange = { twoFactorEnabled = it })
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                SecuritySectionTitle("Privacidad")
                SecurityItem(
                    icon = Icons.Outlined.Visibility,
                    title = "Perfil Público",
                    subtitle = "Permite que otros vean tus logros",
                    trailing = {
                        Switch(checked = publicProfile, onCheckedChange = { publicProfile = it })
                    }
                )
                SecurityItem(
                    icon = Icons.Outlined.History,
                    title = "Historial de Actividad",
                    subtitle = "Gestiona tus registros pasados",
                    onClick = { /* Ver historial */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                SecuritySectionTitle("Datos Personales")
                SecurityItem(
                    icon = Icons.Outlined.Description,
                    title = "Política de Tratamiento de Datos",
                    subtitle = "Cómo gestionamos tu información",
                    trailing = {
                        Switch(checked = showDataProcessing, onCheckedChange = { showDataProcessing = it })
                    }
                )
                SecurityItem(
                    icon = Icons.Outlined.DeleteForever,
                    title = "Eliminar Cuenta",
                    subtitle = "Esta acción es permanente",
                    iconColor = MaterialTheme.colorScheme.error,
                    labelColor = MaterialTheme.colorScheme.error,
                    onClick = { /* Mostrar diálogo de confirmación */ }
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SecuritySectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SecurityItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = labelColor)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            trailing?.invoke() ?: if (onClick != null) {
                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
            } else {}
        }
    }
}
