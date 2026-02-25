package com.gdcj.voluntariadoiiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NosotrosScreen(
    name: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
            .verticalScroll(rememberScrollState())
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Por qué unirte?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Únete a nuestra institución y sé parte de un equipo que impulsa la innovación, la sostenibilidad y el impacto positivo.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Groups,
                    iconColor = Color(0xFFF38B1C),
                    title = "Nuestra historia",
                    description = "Desde nuestros inicios, hemos trabajado en fortalecer la educación, la investigación y los proyectos sociales."
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.RocketLaunch,
                    iconColor = Color(0xFFFFD600),
                    title = "Nuestra misión",
                    description = "Impulsar iniciativas que transformen vidas a través de la educación y la acción social."
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.WaterDrop,
                    iconColor = Color(0xFF2196F3),
                    title = "Lo que nos diferencia",
                    description = "Nos distingue nuestro enfoque integral, combinando conocimiento e innovación."
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Imagen del Laboratorio",
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(Modifier.weight(1f), "+4", "publicaciones científicas internacionales")
                StatCard(Modifier.weight(1f), "+10", "proyectos en desarrollo actualmente")
                StatCard(Modifier.weight(1f), "+500", "beneficiarios directos en programas sociales")
                StatCard(Modifier.weight(1f), "+8", "alianzas estratégicas con instituciones nacionales e internacionales")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Card(
        modifier = modifier.height(240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                description,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, number: String, text: String) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(number, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}
