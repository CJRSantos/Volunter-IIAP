package com.gdcj.voluntariadoiiap.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gdcj.voluntariadoiiap.ui.components.LogoutDialog
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
    val context = LocalContext.current
    
    val videos = listOf(
        VideoItem("01", "Niñas en la Ciencia", "7-gDLBYwAcc", "https://www.youtube.com/watch?v=7-gDLBYwAcc"),
        VideoItem("02", "Logros del IIAP 2025", "oTxwous9uGs", "https://www.youtube.com/watch?v=oTxwous9uGs"),
        VideoItem("03", "Jane Goodall en el IIAP: una voz que inspira", "5xSmXSLrRI0", "https://www.youtube.com/watch?v=5xSmXSLrRI0"),
        VideoItem("04", "Inauguración del IIAP Sede Tingo María", "NMF_35Q4nCU", "https://www.youtube.com/watch?v=NMF_35Q4nCU"),
        VideoItem("05", "APEC 2024: Una Semana Histórica", "5gdt_gml7o4", "https://www.youtube.com/watch?v=5gdt_gml7o4"),
        VideoItem("06", "IIAP", "saEzfUc_JLo", "https://www.youtube.com/watch?v=saEzfUc_JLo")
    )

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
        
        item { SectionHeader("Videos Institucionales IIAP") }
        
        items(videos) { video ->
            VideoCard(video = video, onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                context.startActivity(intent)
            })
        }

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
                text = "Explora las últimas actividades y videos del IIAP.",
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
fun VideoCard(video: VideoItem, onClick: () -> Unit) {
    val thumbnailUrl = "https://img.youtube.com/vi/${video.youtubeId}/mqdefault.jpg"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
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
                    fontSize = 16.sp, 
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Ver video institucional", 
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), 
                    fontSize = 13.sp
                )
            }
        }
    }
}

data class VideoItem(val id: String, val title: String, val youtubeId: String, val url: String)
