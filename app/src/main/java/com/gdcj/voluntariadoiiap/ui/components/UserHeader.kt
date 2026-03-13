package com.gdcj.voluntariadoiiap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdcj.voluntariadoiiap.ui.viewmodel.AuthViewModel

@Composable
fun UserHeader(
    name: String,
    email: String,
    authViewModel: AuthViewModel,
    onMenuClick: () -> Unit
) {
    val profilePictureUri by authViewModel.profilePictureUri.collectAsState()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Avatar Dinámico usando el componente reutilizable
            ProfileAvatar(
                uri = profilePictureUri,
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Bienvenido", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
