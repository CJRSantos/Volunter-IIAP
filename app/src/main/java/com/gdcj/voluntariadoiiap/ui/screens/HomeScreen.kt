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
    onNavigateToProfile: () -> Unit,
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
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    hoveredElevation = 0.dp,
                    focusedElevation = 0.dp
                )
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
            "12/10/2025, Hr: 00-00",
            "¿Qué es el monitoreo de carbono en bosques amazónicos?",
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = newsItem.imageRes,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(newsItem.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(newsItem.date, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                TextButton(onClick = {}) {
                    Text("Leer más")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

data class NewsItem(val title: String, val date: String, val description: String, val imageRes: Int)

@Composable
fun VideoCarousel() {
    val context = LocalContext.current
    val videos = listOf(
        VideoItem("01", "Niñas en la Ciencia", "7-gDLBYwAcc", "https://www.youtube.com/watch?v=7-gDLBYwAcc")
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
        modifier = Modifier.width(240.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(135.dp), contentAlignment = Alignment.Center) {
                AsyncImage(model = thumbnailUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = video.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, maxLines = 1)
                Text(text = "Toca para ver el video", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
    }
}

data class VideoItem(val id: String, val title: String, val youtubeId: String, val url: String)

@Composable
fun SocialMediaList() {
    val context = LocalContext.current
    val socialMedias = listOf(
        SocialMediaData(R.drawable.ic_facebook, "Facebook", Color(0xFF1877F2), "https://www.facebook.com/IIAPPERU/")
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
                modifier = Modifier.width(280.dp).height(180.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

data class SocialMediaData(val icon: Int, val name: String, val containerColor: Color, val url: String)
