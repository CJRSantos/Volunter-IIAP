package com.gdcj.voluntariadoiiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(name: String, email: String) {
    Scaffold(
        bottomBar = { HomeBottomNavigation() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            item { UserHeader(name, email) }
            item { MainBanner() }
            item { SectionHeader("Noticias") }
            item { NewsCarousel() }
            item { SectionHeader("Videos") }
            item { VideoCarousel() }
            item { SectionHeader("Redes Sociales") }
            item { SocialMediaCarousel() }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun UserHeader(name: String, email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Bienvenido", fontSize = 12.sp, color = Color.Gray)
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = email, fontSize = 12.sp, color = Color.Gray)
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    }
}

@Composable
fun MainBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF003366))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Aquí comienza tu espacio exclusivo de usuario",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.width(200.dp)
                )
                Text(
                    text = "Bienvenido, sé parte de este nuevo...",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Conocer más", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun NewsCarousel() {
    val newsList = listOf("Monitoreo de carbono en bosques amazónicos", "Otra noticia del IIAP")
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(newsList) { news ->
            Card(
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = news, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                        Text(text = "12/10/2025, Hr: 00:00", fontSize = 10.sp, color = Color.Gray)
                        Text(text = "¿Qué es el monitoreo de carbono...?", fontSize = 11.sp, maxLines = 2)
                        TextButton(onClick = { /* TODO */ }, contentPadding = PaddingValues(0.dp)) {
                            Text("Leer más →", fontSize = 12.sp, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCarousel() {
    val videos = listOf("Video 1", "Video 2")
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(videos) { video ->
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
                            .height(120.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
                    }
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = video, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Haz clic para ver el video", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun SocialMediaCarousel() {
    val socialNetworks = listOf(
        SocialMedia("Facebook", Color(0xFF1877F2), Icons.Default.Share),
        SocialMedia("Twitter", Color(0xFF1DA1F2), Icons.Default.Email),
        SocialMedia("Instagram", Color(0xFFE4405F), Icons.Default.CameraAlt),
        SocialMedia("Youtube", Color(0xFFFF0000), Icons.Default.PlayCircle),
        SocialMedia("LinkedIn", Color(0xFF0A66C2), Icons.Default.Business),
        SocialMedia("Sitio web", Color(0xFF4CAF50), Icons.Default.Language),
        SocialMedia("TikTok", Color(0xFF000000), Icons.Default.MusicNote),
        SocialMedia("Spotify", Color(0xFF1DB954), Icons.Default.Audiotrack)
    )
    
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(socialNetworks) { social ->
            Card(
                modifier = Modifier.width(160.dp).height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = social.color)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Icon(social.icon, contentDescription = null, tint = Color.White, modifier = Modifier.align(Alignment.TopStart).size(32.dp))
                    Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).size(20.dp))
                    
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text("Síguenos en", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        Text(social.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    
                    Icon(
                        social.icon, 
                        contentDescription = null, 
                        tint = Color.White.copy(alpha = 0.15f), 
                        modifier = Modifier.size(80.dp).align(Alignment.BottomEnd).offset(20.dp, 20.dp)
                    )
                }
            }
        }
    }
}

data class SocialMedia(val name: String, val color: Color, val icon: ImageVector)

@Composable
fun HomeBottomNavigation() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio") },
            selected = true,
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4CAF50), selectedTextColor = Color(0xFF4CAF50), indicatorColor = Color(0xFFE8F5E9))
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Áreas") },
            selected = false,
            onClick = { /* TODO */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            label = { Text("Convocatorias") },
            selected = false,
            onClick = { /* TODO */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Nosotros") },
            selected = false,
            onClick = { /* TODO */ }
        )
    }
}
