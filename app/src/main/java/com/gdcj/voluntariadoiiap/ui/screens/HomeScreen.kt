package com.gdcj.voluntariadoiiap.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.gdcj.voluntariadoiiap.ui.viewmodel.*

data class VideoItem(val id: String, val title: String, val youtubeId: String, val url: String)

@Composable
fun HomeScreen(
    name: String,
    email: String,
    themeViewModel: ThemeViewModel,
    authViewModel: AuthViewModel,
    onLogoutNavigate: () -> Unit,
    onNavigateToInfo: () -> Unit,
    onNavigateToAreas: () -> Unit,
    onProfileClick: () -> Unit,
    areaViewModel: AreaViewModel = viewModel(),
    viewModel: HomeViewModel = viewModel()
) {
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()
    val areaState by areaViewModel.areaListState.collectAsState()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    
    var showAllAreas by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        areaViewModel.fetchAreas()
    }

    val videos = listOf(
        VideoItem("01", "Niñas en la Ciencia", "7-gDLBYwAcc", "https://www.youtube.com/watch?v=7-gDLBYwAcc"),
        VideoItem("02", "Logros del IIAP 2025", "oTxwous9uGs", "https://www.youtube.com/watch?v=oTxwous9uGs"),
        VideoItem("03", "Jane Goodall en el IIAP", "5xSmXSLrRI0", "https://www.youtube.com/watch?v=5xSmXSLrRI0"),
        VideoItem("04", "Inauguración Sede Tingo María", "NMF_35Q4nCU", "https://www.youtube.com/watch?v=NMF_35Q4nCU"),
        VideoItem("05", "APEC 2024: Semana Histórica", "5gdt_gml7o4", "https://www.youtube.com/watch?v=5gdt_gml7o4")
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
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // HERO CARD: INICIATIVA 2026
        item {
            IniciativaHero(
                onExplorarClick = onNavigateToAreas
            )
        }

        // SECCIÓN VIDEOS (CARRUSEL)
        item {
            SectionHeader(title = "Videos Institucionales", showAction = false)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(videos) { video ->
                    VideoCarouselCard(video = video) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                        context.startActivity(intent)
                    }
                }
            }
        }

        // SECCIÓN DESTACADOS (ÁREAS)
        item {
            SectionHeader(
                title = "Destacados", 
                showAction = true, 
                onActionClick = { showAllAreas = !showAllAreas },
                actionLabel = if (showAllAreas) "Ver menos" else "Ver todo"
            )
        }

        when (val state = areaState) {
            is AreaListState.Success -> {
                val areasToShow = if (showAllAreas) state.areas.take(7) else state.areas.take(2)
                itemsIndexed(areasToShow) { index, area ->
                    AreaVerticalCard(
                        title = area.description,
                        location = if (index % 2 == 0) "Iquitos" else "San Martín",
                        tag = if (index % 2 == 0) "Activo" else "Urgente",
                        tagColor = if (index % 2 == 0) Color(0xFFFDE8D7).copy(alpha = if(isDark) 0.3f else 1f) else Color(0xFFFFE0B2).copy(alpha = if(isDark) 0.3f else 1f),
                        tagTextColor = if (index % 2 == 0) Color(0xFFD47321) else Color(0xFFE65100),
                        imageUrl = when(index % 3) {
                            0 -> "https://images.unsplash.com/photo-1543158023-e6293442654a?q=80&w=400"
                            1 -> "https://images.unsplash.com/photo-1511497584788-876760111969?q=80&w=400"
                            else -> "https://images.unsplash.com/photo-1502082553048-f009c37129b9?q=80&w=400"
                        }
                    )
                }
            }
            is AreaListState.Loading -> {
                item { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFFE64A19)) } }
            }
            else -> {
                val mockAreas = listOf("Rescate del Delfín Rosado", "Reforestación Nativa", "Monitoreo de Aves", "Calidad del Agua", "Bosques Inundables", "Suelos Amazónicos", "Peces Ornamentales")
                val areasToShow = if (showAllAreas) mockAreas.take(7) else mockAreas.take(2)
                itemsIndexed(areasToShow) { index, title ->
                    AreaVerticalCard(
                        title = title,
                        location = if (index % 2 == 0) "Iquitos" else "San Martín",
                        tag = if (index % 2 == 0) "Activo" else "Urgente",
                        tagColor = if (index % 2 == 0) Color(0xFFFDE8D7).copy(alpha = if(isDark) 0.3f else 1f) else Color(0xFFFFE0B2).copy(alpha = if(isDark) 0.3f else 1f),
                        tagTextColor = if (index % 2 == 0) Color(0xFFD47321) else Color(0xFFE65100),
                        imageUrl = when(index % 3) {
                            0 -> "https://images.unsplash.com/photo-1543158023-e6293442654a?q=80&w=400"
                            1 -> "https://images.unsplash.com/photo-1511497584788-876760111969?q=80&w=400"
                            else -> "https://images.unsplash.com/photo-1502082553048-f009c37129b9?q=80&w=400"
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun IniciativaHero(onExplorarClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(32.dp))
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1440688807730-73e4e2169fb8?q=80&w=800",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 400f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Surface(
                color = Color(0xFFE64A19),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "INICIATIVA 2026",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Protege el Pulmón del Mundo",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Únete a nuestras iniciativas de conservación y monitoreo de especies en Loreto.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onExplorarClick,
                modifier = Modifier.height(56.dp).fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE64A19)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Explorar Áreas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun VideoCarouselCard(video: VideoItem, onClick: () -> Unit) {
    val thumbnailUrl = "https://img.youtube.com/vi/${video.youtubeId}/mqdefault.jpg"
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
            Surface(
                modifier = Modifier.size(48.dp).align(Alignment.Center),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.padding(12.dp))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
                    .padding(16.dp)
            ) {
                Text(
                    text = video.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AreaVerticalCard(title: String, location: String, tag: String, tagColor: Color, tagTextColor: Color, imageUrl: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Proyecto de monitoreo en la...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = tagColor, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = tagTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = location, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, showAction: Boolean, onActionClick: (() -> Unit)? = null, actionLabel: String = "Ver todo") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (showAction) {
            Text(
                text = actionLabel,
                modifier = Modifier.clickable { onActionClick?.invoke() },
                color = Color(0xFFE64A19),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
