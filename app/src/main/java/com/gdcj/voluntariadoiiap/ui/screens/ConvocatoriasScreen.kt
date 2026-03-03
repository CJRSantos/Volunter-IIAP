package com.gdcj.voluntariadoiiap.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Convocatoria(
    val id: Int,
    val title: String,
    val area: String,
    val location: String,
    val deadline: String,
    val description: String,
    val status: String // "Abierta", "Cerrada", "Próximamente"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvocatoriasScreen() {
    var searchQuery by remember { mutableStateOf("") }
    
    val allConvocatorias = listOf(
        Convocatoria(1, "Especialista en Fauna Silvestre", "Diversidad Biológica", "Iquitos", "15 Nov 2025", "Buscamos voluntarios para apoyo en el monitoreo de jaguares en la reserva.", "Abierta"),
        Convocatoria(2, "Analista de Suelos Amazónicos", "Manejo Forestal", "Pucallpa", "20 Nov 2025", "Investigación sobre la degradación de suelos en áreas deforestadas.", "Abierta"),
        Convocatoria(3, "Gestor de Base de Datos", "Gestión del Conocimiento", "Iquitos", "01 Dic 2025", "Apoyo en la digitalización de archivos históricos del IIAP.", "Próximamente"),
        Convocatoria(4, "Monitor de Calidad de Agua", "Ecosistemas Acuáticos", "Tarapoto", "10 Nov 2025", "Toma de muestras en la cuenca del río Huallaga.", "Abierta"),
        Convocatoria(5, "Asistente de Antropología", "Sociedades Amazónicas", "Iquitos", "05 Nov 2025", "Trabajo de campo con comunidades nativas del Napo.", "Cerrada")
    )

    val filteredConvocatorias = allConvocatorias.filter { 
        it.title.contains(searchQuery, ignoreCase = true) || it.area.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Cabecera con Búsqueda
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Convocatorias",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Encuentra tu oportunidad ideal",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por título o área...", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredConvocatorias, key = { it.id }) { convocatoria ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500)) + expandVertically()
                ) {
                    ConvocatoriaCard(convocatoria)
                }
            }
        }
    }
}

@Composable
fun ConvocatoriaCard(convocatoria: Convocatoria) {
    val statusColor = when(convocatoria.status) {
        "Abierta" -> Color(0xFF4CAF50)
        "Cerrada" -> Color(0xFFE91E63)
        else -> Color(0xFFFF9800)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = convocatoria.status,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = convocatoria.area,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = convocatoria.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = convocatoria.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(convocatoria.location, fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cierra: ${convocatoria.deadline}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = convocatoria.status == "Abierta"
            ) {
                Text("Postular ahora")
            }
        }
    }
}
