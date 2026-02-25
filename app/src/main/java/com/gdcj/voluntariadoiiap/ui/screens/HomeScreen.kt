package com.gdcj.voluntariadoiiap.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gdcj.voluntariadoiiap.R
import com.gdcj.voluntariadoiiap.ui.components.LogoutDialog
import com.gdcj.voluntariadoiiap.ui.components.UserHeader
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
            .background(Color(0xFFF8F9FA))
    ) {
        item { UserHeader(name = name, email = email, onLogoutClick = { viewModel.onLogoutClick() }) }
        item { SectionHeader("Noticias") }
        item { NewsCarousel() }
        item { SectionHeader("Videos") }
        item { VideoCarousel() }
        item { SectionHeader("Redes Sociales") }
        item { SocialMediaCarousel() }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun NewsCarousel() {
    val newsItems = listOf(
        NewsItem("Monitoreo de carbono en bosques amazónicos", "12/10/2025, Hr: 00-00", "1) ¿Qué es el monitoreo de carbono en bosques amazónicos?", R.drawable.ic_launcher_background),
        NewsItem("Otra noticia", "13/10/2025, Hr: 10-00", "Descripción de otra noticia.", R.drawable.ic_launcher_background)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                Text(text = newsItem.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = newsItem.date, color = Color.Gray, fontSize = 12.sp)
                Text(text = newsItem.description, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                TextButton(onClick = { /*TODO*/ }) {
                    Text("Leer más")
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

data class NewsItem(val title: String, val date: String, val description: String, val imageRes: Int)

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = video.thumbnailRes),
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
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = video.title, fontWeight = FontWeight.Bold)
                Text(text = video.caption, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

data class VideoItem(val title: String, val caption: String, val thumbnailRes: Int)

@Composable
fun SocialMediaCarousel() {
    val socialItems = listOf(
        SocialMediaItem("Síguenos en", "Facebook", Color(0xFF1877F2)),
        SocialMediaItem("Síguenos en", "Twitter", Color(0xFF1DA1F2))
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(socialItems) { item ->
            SocialMediaCard(item)
        }
    }
}

@Composable
fun SocialMediaCard(item: SocialMediaItem) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = item.color),
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                 Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.platform.first().toString(), color = item.color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Column {
                    Text(text = item.action, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    Text(text = item.platform, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
             Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

data class SocialItem(val platform: String, val color: Color)
data class SocialMediaItem(val action: String, val platform: String, val color: Color)
