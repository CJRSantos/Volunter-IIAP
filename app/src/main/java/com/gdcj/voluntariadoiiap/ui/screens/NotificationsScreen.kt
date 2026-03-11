package com.gdcj.voluntariadoiiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.gdcj.voluntariadoiiap.data.model.Notification
import com.gdcj.voluntariadoiiap.data.model.NotificationType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBackClick: () -> Unit) {
    // Datos de ejemplo
    val notifications = remember {
        mutableStateListOf(
            Notification(1, "¡Logro Desbloqueado!", "Has ganado la medalla 'Colaborador Oro' por tus 100 horas.", System.currentTimeMillis() - 3600000, false, NotificationType.ACHIEVEMENT_UNLOCKED),
            Notification(2, "Nueva Convocatoria", "Se ha publicado un nuevo proyecto de reforestación en Iquitos.", System.currentTimeMillis() - 86400000, true, NotificationType.PROJECT_UPDATE),
            Notification(3, "Postulación Aceptada", "Tu solicitud para el proyecto 'Amazonía Viva' ha sido aprobada.", System.currentTimeMillis() - 172800000, false, NotificationType.APPLICATION_STATUS),
            Notification(4, "Recordatorio de Seguridad", "Te recomendamos cambiar tu contraseña cada 3 meses.", System.currentTimeMillis() - 259200000, true, NotificationType.GENERAL)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    TextButton(onClick = { /* Marcar todas como leídas */ }) {
                        Text("Leer todas", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            EmptyNotifications()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(notification)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    val backgroundColor = if (notification.isRead) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    val icon = when (notification.type) {
        NotificationType.ACHIEVEMENT_UNLOCKED -> Icons.Default.EmojiEvents
        NotificationType.PROJECT_UPDATE -> Icons.Default.Assignment
        NotificationType.APPLICATION_STATUS -> Icons.Default.CheckCircle
        NotificationType.GENERAL -> Icons.Default.Notifications
    }
    val iconColor = when (notification.type) {
        NotificationType.ACHIEVEMENT_UNLOCKED -> Color(0xFFFFD700)
        NotificationType.PROJECT_UPDATE -> MaterialTheme.colorScheme.primary
        NotificationType.APPLICATION_STATUS -> Color(0xFF4CAF50)
        NotificationType.GENERAL -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(12.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = notification.title,
                    fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (!notification.isRead) {
                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                }
            }
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
            Text(
                text = formatTimestamp(notification.timestamp),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun EmptyNotifications() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.NotificationsOff, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No tienes notificaciones", color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
