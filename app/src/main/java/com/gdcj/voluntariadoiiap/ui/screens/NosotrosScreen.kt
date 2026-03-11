package com.gdcj.voluntariadoiiap.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdcj.voluntariadoiiap.R
import com.gdcj.voluntariadoiiap.ui.components.SocialMediaItem

@Composable
fun NosotrosScreen(name: String, email: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "¿Por qué unirte?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Únete a nuestra institución y sé parte de un equipo que impulsa la innovación, la sostenibilidad y el impacto positivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReasonCard(
                    icon = Icons.Outlined.Groups,
                    title = "Nuestra historia",
                    description = "Desde nuestros inicios, hemos trabajado para fortalecer la educación e investigación.",
                    modifier = Modifier.weight(1f)
                )
                ReasonCard(
                    icon = Icons.Outlined.RocketLaunch,
                    title = "Nuestra misión",
                    description = "Impulsar iniciativas que transforman vidas a través de la educación.",
                    modifier = Modifier.weight(1f)
                )
                ReasonCard(
                    icon = Icons.Outlined.WaterDrop,
                    title = "Lo que nos diferencia",
                    description = "Nos distingue nuestro enfoque integral e innovador.",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Investigación para el Desarrollo Sostenible", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }

        item {
             Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(value = "+4", label = "publicaciones científicas", modifier = Modifier.weight(1f))
                StatCard(value = "+10", label = "proyectos en desarrollo", modifier = Modifier.weight(1f))
                StatCard(value = "+500", label = "beneficiarios directos", modifier = Modifier.weight(1f))
                StatCard(value = "+8", label = "alianzas estratégicas", modifier = Modifier.weight(1f))
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nuestras Redes Sociales",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                SocialMediaList()
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun SocialMediaList() {
    val context = LocalContext.current
    val socialMedias = listOf(
        SocialMediaData(R.drawable.ic_facebook, "Facebook", Color(0xFF1877F2), "https://www.facebook.com/IIAPPERU/"),
        SocialMediaData(R.drawable.ic_instagram, "Instagram", Color(0xFFE4405F), "https://www.instagram.com/iiapperu/?hl=es"),
        SocialMediaData(R.drawable.ic_x, "X (Twitter)", Color(0xFF000000), "https://x.com/IiapPeru"),
        SocialMediaData(R.drawable.ic_spotify, "Spotify", Color(0xFF1DB954), "https://open.spotify.com/show/22EKStrMUkA8MciXSj9EaE?si=4e51ebc4ad974d4b"),
        SocialMediaData(R.drawable.ic_linkedin, "LinkedIn", Color(0xFF0A66C2), "https://pe.linkedin.com/company/instituto-de-investigaci%C3%B3n-de-la-amazon%C3%ADa-peruana-iiap"),
        SocialMediaData(R.drawable.ic_youtube, "YouTube", Color(0xFFFF0000), "https://www.youtube.com/channel/UC7h_V_SOwW0wRsmhf00lAsA"),
        SocialMediaData(R.drawable.ic_tiktok, "TikTok", Color(0xFF000000), "https://www.tiktok.com/@iiapperu?_r=1&_t=ZS-94FGzy7jFOq"),
        SocialMediaData(R.drawable.ic_web, "Sitio Web", Color(0xFF008000), "https://www.gob.pe/iiap")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(socialMedias) { data ->
            SocialMediaItem(
                icon = data.icon,
                name = data.name,
                containerColor = data.containerColor,
                modifier = Modifier.width(180.dp).height(100.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

data class SocialMediaData(val icon: Int, val name: String, val containerColor: Color, val url: String)

@Composable
private fun ReasonCard(icon: ImageVector, title: String, description: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
     Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
     ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(text = label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = Color.Gray)
        }
    }
}
