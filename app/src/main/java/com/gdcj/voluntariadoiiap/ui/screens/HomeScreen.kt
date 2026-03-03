package com.gdcj.voluntariadoiiap.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gdcj.voluntariadoiiap.R
import com.gdcj.voluntariadoiiap.ui.components.LogoutDialog
import com.gdcj.voluntariadoiiap.ui.components.UserHeader
import com.gdcj.voluntariadoiiap.ui.components.SocialMediaItem
import com.gdcj.voluntariadoiiap.ui.viewmodel.HomeViewModel
import com.gdcj.voluntariadoiiap.ui.viewmodel.ThemeViewModel

@Composable
fun HomeScreen(
    name: String,
    email: String,
    themeViewModel: ThemeViewModel,
    onLogoutNavigate: () -> Unit,
    onNavigateToInfo: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()

    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { viewModel.onDismissLogoutDialog() },
            onConfirm = {
                viewModel.onConfirmLogout()
                onLogoutNavigate()
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item { 
            UserHeader(
                name = name, 
                email = email, 
                themeViewModel = themeViewModel,
                onLogoutClick = { viewModel.onLogoutClick() }
            ) 
        }

        // Hero Section / Tarjeta de Bienvenida
        item { WelcomeHeroCard(onActionClick = onNavigateToInfo) }

        item { SectionHeader("Noticias") }
        item { NewsCarousel() }

        item { SectionHeader("Videos") }
        item { VideoCarousel() }

        item { SectionHeader("Redes Sociales") }
        item { SocialMediaList() }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

/* -------------------- HEADERS -------------------- */

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/* -------------------- HERO SECTION -------------------- */

@Composable
fun WelcomeHeroCard(onActionClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Aquí comienza tu espacio exclusivo de usuario",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido, sé parte de este nuevo...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Conocer más",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* -------------------- NOTICIAS -------------------- */

@Composable
fun NewsCarousel() {
    val newsItems = listOf(
        NewsItem(
            "Monitoreo de carbono en bosques amazónicos",
            "12/10/2025, Hr: 00-00",
            "¿Qué es el monitoreo de carbono en bosques amazónicos?",
            R.drawable.ic_launcher_background
        ),
        NewsItem(
            "Otra noticia",
            "13/10/2025, Hr: 10-00",
            "Descripción de otra noticia.",
            R.drawable.ic_launcher_background
        )
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(newsItems) { item ->
            NewsCard(item)
        }
    }
}

@Composable
fun NewsCard(newsItem: NewsItem) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = newsItem.imageRes,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(newsItem.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(newsItem.date, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                Text(newsItem.description, fontSize = 12.sp)
                TextButton(onClick = {}) {
                    Text("Leer más")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

data class NewsItem(
    val title: String,
    val date: String,
    val description: String,
    val imageRes: Int
)

/* -------------------- VIDEOS -------------------- */

@Composable
fun VideoCarousel() {
    val context = LocalContext.current

    val videos = listOf(
        VideoItem(
            id = "01",
            title = "Niñas en la Ciencia",
            youtubeId = "7-gDLBYwAcc",
            url = "https://www.youtube.com/watch?v=7-gDLBYwAcc"
        ),
        VideoItem(
            id = "02",
            title = "Logros del IIAP en el 2025",
            youtubeId = "oTxwous9uGs",
            url = "https://www.youtube.com/watch?v=oTxwous9uGs"
        ),
        VideoItem(
            id = "03",
            title = "Jane Goodall en el IIAP: una voz que inspira",
            youtubeId = "5xSmXSLrRI0",
            url = "https://www.youtube.com/watch?v=5xSmXSLrRI0"
        ),
        VideoItem(
            id = "04",
            title = "Inauguración del IIAP Sede Tingo María",
            youtubeId = "NMF_35Q4nCU",
            url = "https://www.youtube.com/watch?v=NMF_35Q4nCU"
        ),
        VideoItem(
            id = "05",
            title = "APEC 2024: Una Semana Histórica",
            youtubeId = "5gdt_gml7o4",
            url = "https://www.youtube.com/watch?v=5gdt_gml7o4"
        ),
        VideoItem(
            id = "06",
            title = "IIAP",
            youtubeId = "saEzfUc_JLo",
            url = "https://www.youtube.com/watch?v=saEzfUc_JLo"
        )
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(videos) { video ->
            VideoCard(
                video = video,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun VideoCard(video: VideoItem, onClick: () -> Unit) {
    val thumbnailUrl = "https://img.youtube.com/vi/${video.youtubeId}/mqdefault.jpg"

    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background)
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "ID: ${video.id}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = video.title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Text(
                    text = "Toca para ver el video",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

data class VideoItem(
    val id: String,
    val title: String,
    val youtubeId: String,
    val url: String
)

/* -------------------- REDES SOCIALES (PRO) -------------------- */

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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(socialMedias) { data ->
            SocialMediaItem(
                icon = data.icon,
                name = data.name,
                containerColor = data.containerColor,
                modifier = Modifier
                    .width(280.dp)
                    .height(180.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

data class SocialMediaData(
    val icon: Int,
    val name: String,
    val containerColor: Color,
    val url: String
)
