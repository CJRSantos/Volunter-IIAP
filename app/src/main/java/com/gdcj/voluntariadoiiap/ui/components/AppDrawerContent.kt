package com.gdcj.voluntariadoiiap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gdcj.voluntariadoiiap.ui.viewmodel.AuthViewModel

@Composable
fun AppDrawerContent(
    name: String,
    email: String,
    authViewModel: AuthViewModel,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val profilePictureUri by authViewModel.profilePictureUri.collectAsState()

    Surface(
        modifier = Modifier.fillMaxHeight().width(300.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Header del Drawer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePictureUri != null) {
                        AsyncImage(
                            model = profilePictureUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Opciones del Menu
            DrawerItem(
                icon = Icons.Default.PersonOutline,
                label = "Perfil",
                onClick = onProfileClick
            )
            
            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            DrawerItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                label = "Cerrar sesión",
                iconColor = Color(0xFFE57373),
                labelColor = Color(0xFFE57373),
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = labelColor
            )
        }
    }
}
