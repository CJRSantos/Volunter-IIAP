package com.gdcj.voluntariadoiiap.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gdcj.voluntariadoiiap.data.model.Project
import com.gdcj.voluntariadoiiap.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvocatoriasScreen(
    projectViewModel: ProjectViewModel,
    authViewModel: AuthViewModel,
    applicationViewModel: ApplicationViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val projectState by projectViewModel.projectListState.collectAsState()
    val applicationState by applicationViewModel.operationState.collectAsState()
    val context = LocalContext.current

    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var showApplyDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        projectViewModel.fetchProjects()
    }

    LaunchedEffect(applicationState) {
        when (applicationState) {
            is ApplicationOperationState.Success -> {
                Toast.makeText(context, (applicationState as ApplicationOperationState.Success).message, Toast.LENGTH_LONG).show()
                applicationViewModel.resetState()
                showApplyDialog = false
            }
            is ApplicationOperationState.Error -> {
                Toast.makeText(context, (applicationState as ApplicationOperationState.Error).message, Toast.LENGTH_LONG).show()
                applicationViewModel.resetState()
            }
            else -> {}
        }
    }

    if (showApplyDialog && selectedProject != null) {
        ApplyDialog(
            project = selectedProject!!,
            onDismiss = { showApplyDialog = false },
            onConfirm = { motivation ->
                val token = authViewModel.sessionManager.fetchAuthToken() ?: ""
                // Nota: El user_id debería venir del perfil del usuario logueado. 
                // Por ahora usamos un ID genérico o deberíamos obtenerlo del AuthViewModel si estuviera disponible.
                // Idealmente la API debería identificar al usuario por el Token.
                applicationViewModel.applyToProject(
                    token = token,
                    userId = 1, // <--- TODO: Obtener el ID real del usuario logueado
                    projectId = selectedProject?.id ?: 0,
                    motivation = motivation
                )
            },
            isLoading = applicationState is ApplicationOperationState.Loading
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Cabecera con Búsqueda
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Convocatorias",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Encuentra tu oportunidad ideal",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por título...", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = projectState) {
                is ProjectListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProjectListState.Success -> {
                    val filteredProjects = state.projects.filter {
                        it.name?.contains(searchQuery, ignoreCase = true) == true ||
                                it.description?.contains(searchQuery, ignoreCase = true) == true
                    }

                    if (filteredProjects.isEmpty()) {
                        Text(
                            text = "No hay convocatorias disponibles",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredProjects, key = { it.id ?: 0 }) { project ->
                                Column {
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn(animationSpec = tween(500)) + expandVertically()
                                    ) {
                                        ConvocatoriaCard(
                                            project = project,
                                            onApplyClick = {
                                                selectedProject = project
                                                showApplyDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is ProjectListState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = { projectViewModel.fetchProjects() }) {
                            Text("Reintentar")
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ConvocatoriaCard(project: Project, onApplyClick: () -> Unit) {
    val status = "Abierta"
    val statusColor = Color(0xFF4CAF50)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Amazonía", 
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = project.name ?: "Sin título",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.description ?: "Sin descripción",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sede IIAP", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cierra: ${project.endDate}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onApplyClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Postular ahora")
            }
        }
    }
}

@Composable
fun ApplyDialog(
    project: Project,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    isLoading: Boolean
) {
    var motivation by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Postular a: ${project.name}") },
        text = {
            Column {
                Text(
                    text = "¿Por qué deseas participar en este proyecto?",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = motivation,
                    onValueChange = { motivation = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Escribe tu motivación aquí...") },
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(motivation) },
                enabled = motivation.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Enviar Postulación")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}
