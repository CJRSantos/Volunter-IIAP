package com.gdcj.voluntariadoiiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AreasScreen(
    name: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Título de la pantalla
        Text(
            text = "Áreas",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Lista de datos de las áreas
        val areas = listOf(
            AreaItem(
                title = "Diversidad Biológica Terrestre Amazónica",
                subtitle = "Dirección de Investigación en Diversidad Biológica",
                description = "Investigación sobre plantas, fauna y biodiversidad terrestre amazónica."
            ),
            AreaItem(
                title = "Ecosistemas Acuáticos Amazónicos",
                subtitle = "Dirección de Investigación en Ecosistemas Acuáticos",
                description = "Estudios sobre ambientes acuáticos, pesca sostenible, ictiología y recursos del río."
            ),
            AreaItem(
                title = "Manejo Integral del Bosque y Servicios Ecosistémicos",
                subtitle = "Dirección de Manejo Forestal",
                description = "Investigación sobre bosques, servicios ecológicos y aprovechamiento de recursos forestales."
            ),
            AreaItem(
                title = "Sociedades Amazónicas",
                subtitle = "Dirección de Investigaciones Sociales",
                description = "Estudios sobre culturas, comunidades indígenas, aspectos sociales y económicos amazónicos."
            ),
            AreaItem(
                title = "Información y Gestión del Conocimiento",
                subtitle = "Oficina de Gestión de Información",
                description = "Gestión de datos, sistemas de información científica, bancos de información y apoyo al conocimiento."
            )
        )

        // Carrusel vertical de widgets
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(areas) { area ->
                AreaCardWidget(area)
            }
        }
    }
}

@Composable
fun AreaCardWidget(area: AreaItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            // Chip de Ubicación (Estilo igual a la imagen)
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.wrapContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sin ubicación",
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtítulo (Dirección)
            Text(
                text = area.subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )

            // Título Principal
            Text(
                text = "Área de ${area.title}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 6.dp),
                lineHeight = 24.sp
            )

            // Descripción
            Text(
                text = area.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

data class AreaItem(
    val title: String,
    val subtitle: String,
    val description: String
)
