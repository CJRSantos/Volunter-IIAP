package com.gdcj.voluntariadoiiap.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.gdcj.voluntariadoiiap.R
import com.gdcj.voluntariadoiiap.ui.components.LogoutDialog
import com.gdcj.voluntariadoiiap.ui.components.UserHeader
import com.gdcj.voluntariadoiiap.ui.components.SocialMediaItem
import com.gdcj.voluntariadoiiap.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    name: String,
    email: String,
    onLogoutNavigate: () -> Unit,
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
        item { UserHeader(name = name, email = email, onLogoutClick = { viewModel.onLogoutClick() }) }

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
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
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
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = newsItem.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(newsItem.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(newsItem.date, color = Color.Gray, fontSize = 12.sp)
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
    val videos = listOf(
        VideoItem("Video 1", "Haz clic para ver el video", R.drawable.ic_launcher_background),
        VideoItem("Video 2", "Haz clic para ver el video", R.drawable.ic_launcher_background)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(videos) { video ->
            VideoCard(video)
        }
    }
}

@Composable
fun VideoCard(video: VideoItem) {
    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(video.thumbnailRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(video.title, fontWeight = FontWeight.Bold)
                Text(video.caption, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

data class VideoItem(
    val title: String,
    val caption: String,
    val thumbnailRes: Int
)

/* -------------------- REDES SOCIALES (PRO) -------------------- */

@Composable
fun SocialMediaList() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SocialMediaItem(
            icon = R.drawable.ic_facebook,
            name = "Facebook",
            containerColor = Color(0xFF1877F2),
            contentColor = Color.White,
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
                )
            }
        )

        SocialMediaItem(
            icon = R.drawable.ic_instagram,
            name = "Instagram",
            containerColor = Color(0xFFE4405F),
            contentColor = Color.White,
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"))
                )
            }
        )

        SocialMediaItem(
            icon = R.drawable.ic_x,
            name = "X (Twitter)",
            containerColor = Color.Black,
            contentColor = Color.White,
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://x.com"))
                )
            }
        )
    }
}
