package com.gdcj.voluntariadoiiap.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdcj.voluntariadoiiap.R

@Composable
fun NosotrosScreen(name: String, email: String) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Hero Section with Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo_iiap),
                    contentDescription = "Logo IIAP",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Investigación para la Amazonía",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // Intro Section
            Text(
                text = "¿Quiénes Somos?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "El Instituto de Investigaciones de la Amazonía Peruana (IIAP) es una institución dedicada a la investigación científica y tecnológica para el desarrollo sostenible de la región amazónica.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Values/Reasons Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    icon = Icons.Outlined.History,
                    title = "Historia",
                    content = "Más de 40 años generando conocimiento científico.",
                    containerColor = Color(0xFFE3F2FD),
                    iconColor = Color(0xFF1976D2),
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    icon = Icons.Outlined.Lightbulb,
                    title = "Visión",
                    content = "Amazonía próspera y sostenible al 2030.",
                    containerColor = Color(0xFFF1F8E9),
                    iconColor = Color(0xFF388E3C),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Impact/Stats Section
            Text(
                text = "Nuestro Impacto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                item { ImpactStat("+40", "Años de experiencia", Icons.Default.CalendarToday) }
                item { ImpactStat("+500", "Proyectos realizados", Icons.Default.Science) }
                item { ImpactStat("+8", "Sedes regionales", Icons.Default.LocationOn) }
                item { ImpactStat("+1K", "Publicaciones", Icons.AutoMirrored.Filled.MenuBook) }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Call to action card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Icon(
                        Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Forma parte del cambio",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Tu talento puede ayudar a preservar el pulmón del mundo. Únete como voluntario hoy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Social Media
            Text(
                text = "Conéctate con nosotros",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            SocialMediaList()

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun InfoCard(
    icon: ImageVector,
    title: String,
    content: String,
    containerColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ImpactStat(value: String, label: String, icon: ImageVector) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SocialMediaList() {
    val context = LocalContext.current
    val socialMedias = listOf(
        SocialMediaData(R.drawable.ic_facebook, "Facebook", Color(0xFF1877F2), "https://www.facebook.com/IIAPPERU/"),
        SocialMediaData(R.drawable.ic_instagram, "Instagram", Color(0xFFE4405F), "https://www.instagram.com/iiapperu/?hl=es"),
        SocialMediaData(R.drawable.ic_x, "X", Color(0xFF000000), "https://x.com/IiapPeru"),
        SocialMediaData(R.drawable.ic_youtube, "YouTube", Color(0xFFFF0000), "https://www.youtube.com/channel/UC7h_V_SOwW0wRsmhf00lAsA"),
        SocialMediaData(R.drawable.ic_tiktok, "TikTok", Color(0xFF000000), "https://www.tiktok.com/@iiapperu"),
        SocialMediaData(R.drawable.ic_linkedin, "LinkedIn", Color(0xFF0A66C2), "https://pe.linkedin.com/company/instituto-de-investigaci%C3%B3n-de-la-amazon%C3%ADa-peruana-iiap"),
        SocialMediaData(R.drawable.ic_web, "Web", Color(0xFF008000), "https://www.gob.pe/iiap")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(socialMedias) { data ->
            Card(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                    context.startActivity(intent)
                },
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = data.containerColor.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, data.containerColor.copy(alpha = 0.2f))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = data.icon),
                        contentDescription = data.name,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

data class SocialMediaData(val icon: Int, val name: String, val containerColor: Color, val url: String)
