package com.gdcj.voluntariadoiiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
            .background(Color(0xFFF8F9FA))
    ) {
        Text(
            text = "Áreas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

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

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(areas) { area ->
                AreaCard(area)
            }
        }
    }
}

@Composable
fun AreaCard(area: AreaItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Surface(
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.wrapContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sin ubicación",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = area.subtitle,
                fontSize = 11.sp,
                color = Color.Gray
            )

            Text(
                text = "Área de ${area.title}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = area.description,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )
        }
    }
}

data class AreaItem(
    val title: String,
    val subtitle: String,
    val description: String
)
