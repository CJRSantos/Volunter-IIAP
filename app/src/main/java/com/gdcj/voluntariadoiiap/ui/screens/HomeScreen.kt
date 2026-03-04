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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gdcj.voluntariadoiiap.R
import com.gdcj.voluntariadoiiap.ui.components.LogoutDialog
import com.gdcj.voluntariadoiiap.ui.components.SocialMediaItem
import com.gdcj.voluntariadoiiap.ui.viewmodel.AuthViewModel
import com.gdcj.voluntariadoiiap.ui.viewmodel.HomeViewModel
import com.gdcj.voluntariadoiiap.ui.viewmodel.ThemeViewModel

@Composable
fun HomeScreen(
    name: String,
    email: String,
    themeViewModel: ThemeViewModel,
    authViewModel: AuthViewModel,
    onLogoutNavigate: () -> Unit,
    onNavigateToInfo: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()

    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { viewModel.onDismissLogoutDialog() },
            onConfirm = {
                authViewModel.logout {
                    viewModel.onConfirmLogout()
                    onLogoutNavigate()
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quitamos el UserHeader de aquí porque ya está en el Scaffold de AppNavigation
        item { WelcomeHeroCard(onActionClick = onNavigateToInfo) }
        
        item { SectionHeader("Noticias Recientes") }
        item { NewsCarousel() }

        item { SectionHeader("Videos IIAP") }
        item { VideoCarousel() }

        item { SectionHeader("Nuestras Redes") }
        item { SocialMediaList() }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun WelcomeHeroCard(onActionClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Tu espacio exclusivo de voluntariado",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explora las últimas noticias y actividades del IIAP.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
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

@Composable
fun NewsCarousel() {
    val newsItems = listOf(
        NewsItem(
            "Monitoreo de carbono en bosques amazónicos",
            "Hace 2 horas",
            "https://www.iiap.org.pe/Archivos/Noticias/Banner_Carbono.jpg"
        ),
        NewsItem(
            "Nuevas especies descubiertas en Loreto",
            "Ayer",
            "https://www.iiap.org.pe/Archivos/Noticias/Especies.jpg"
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
        modifier = Modifier.width(300.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = newsItem.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_background)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = newsItem.title, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = newsItem.date, 
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), 
                    fontSize = 12.sp
                )
                TextButton(
                    onClick = {},
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Leer más", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

data class NewsItem(val title: String, val date: String, val imageUrl: String)

@Composable
fun VideoCarousel() {
    val context = LocalContext.current
    val videos = listOf(
        VideoItem("01", "Niñas en la Ciencia", "7-gDLBYwAcc", "https://www.youtube.com/watch?v=7-gDLBYwAcc"),
        VideoItem("02", "Logros del IIAP 2025", "oTxwous9uGs", "https://www.youtube.com/watch?v=oTxwous9uGs"),
        VideoItem("03", "Jane Goodall en el IIAP: una voz que inspira", "5xSmXSLrRI0", "https://www.youtube.com/watch?v=5xSmXSLrRI0"),
        VideoItem("04", "Inauguración del IIAP Sede Tingo María", "NMF_35Q4nCU", "https://www.youtube.com/watch?v=NMF_35Q4nCU"),
        VideoItem("05", "APEC 2024: Una Semana Histórica", "5gdt_gml7o4", "https://www.youtube.com/watch?v=5gdt_gml7o4"),
        VideoItem("06", "IIAP", "saEzfUc_JLo", "https://www.youtube.com/watch?v=saEzfUc_JLo")
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(videos) { video ->
            VideoCard(video = video, onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                context.startActivity(intent)
            })
        }
    }
}

@Composable
fun VideoCard(video: VideoItem, onClick: () -> Unit) {
    val thumbnailUrl = "https://img.youtube.com/vi/${video.youtubeId}/mqdefault.jpg"
    Card(
        modifier = Modifier.width(260.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(145.dp), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = thumbnailUrl, 
                    contentDescription = null, 
                    modifier = Modifier.fillMaxSize(), 
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = video.title, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 15.sp, 
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Ver video institucional", 
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), 
                    fontSize = 12.sp
                )
            }
        }
    }
}

data class VideoItem(val id: String, val title: String, val youtubeId: String, val url: String)

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
                modifier = Modifier.width(260.dp).height(160.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

data class SocialMediaData(val icon: Int, val name: String, val containerColor: Color, val url: String)
