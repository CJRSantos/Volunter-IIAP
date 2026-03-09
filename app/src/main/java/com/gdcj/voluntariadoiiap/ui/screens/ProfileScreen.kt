package com.gdcj.voluntariadoiiap.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdcj.voluntariadoiiap.data.model.Experience
import com.gdcj.voluntariadoiiap.data.model.Study
import com.gdcj.voluntariadoiiap.data.model.User
import com.gdcj.voluntariadoiiap.ui.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

// Enum para identificar qué formulario mostrar en el BottomSheet
enum class SheetType { PERSONAL, STUDY, EXPERIENCE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    name: String,
    email: String,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    studyViewModel: StudyViewModel,
    experienceViewModel: ExperienceViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val token = authViewModel.sessionManager.fetchAuthToken() ?: ""
    val userId = authViewModel.userId.collectAsState().value
    
    val userDetailState by userViewModel.userDetailState.collectAsState()
    val userStudies by userViewModel.userStudies.collectAsState()
    val userExperiences by userViewModel.userExperiences.collectAsState()
    
    // Observar estados de operación de los ViewModels
    val userOpState by userViewModel.operationState.collectAsState()
    val studyOpState by studyViewModel.operationState.collectAsState()
    val expOpState by experienceViewModel.operationState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Perfil", "Formación", "Experiencia", "Logros")
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheetType by remember { mutableStateOf<SheetType?>(null) }

    // Sincronizar con el backend al iniciar
    LaunchedEffect(userId) {
        if (userId != -1) {
            userViewModel.fetchUserById(token, userId)
        }
    }

    // Manejo de resultados de operaciones para Usuario
    LaunchedEffect(userOpState) {
        if (userOpState is OperationState.Success) {
            Toast.makeText(context, (userOpState as OperationState.Success).message, Toast.LENGTH_SHORT).show()
            userViewModel.resetOperationState()
            if (userId != -1) {
                userViewModel.fetchUserById(token, userId)
            }
        } else if (userOpState is OperationState.Error) {
            Toast.makeText(context, (userOpState as OperationState.Error).message, Toast.LENGTH_SHORT).show()
            userViewModel.resetOperationState()
        }
    }

    // Manejo de resultados de operaciones para Estudios
    LaunchedEffect(studyOpState) {
        if (studyOpState is OperationState.Success) {
            Toast.makeText(context, (studyOpState as OperationState.Success).message, Toast.LENGTH_SHORT).show()
            studyViewModel.resetOperationState()
            if (userId != -1) {
                userViewModel.fetchUserStudies(token, userId)
            }
        } else if (studyOpState is OperationState.Error) {
            Toast.makeText(context, (studyOpState as OperationState.Error).message, Toast.LENGTH_SHORT).show()
            studyViewModel.resetOperationState()
        }
    }

    // Manejo de resultados de operaciones para Experiencia
    LaunchedEffect(expOpState) {
        if (expOpState is OperationState.Success) {
            Toast.makeText(context, (expOpState as OperationState.Success).message, Toast.LENGTH_SHORT).show()
            experienceViewModel.resetOperationState()
            if (userId != -1) {
                userViewModel.fetchUserExperiences(token, userId)
            }
        } else if (expOpState is OperationState.Error) {
            Toast.makeText(context, (expOpState as OperationState.Error).message, Toast.LENGTH_SHORT).show()
            experienceViewModel.resetOperationState()
        }
    }

    // Datos del usuario (con valores por defecto mientras carga)
    val currentUser = (userDetailState as? UserDetailState.Success)?.user
    val profileName = currentUser?.name ?: name
    val profileEmail = currentUser?.email ?: email
    val birthDate = currentUser?.birthDate ?: "Sin especificar"
    val phone = currentUser?.phone ?: "Sin especificar"
    val gender = currentUser?.gender ?: "Sin especificar"
    val location = currentUser?.location ?: "Sin especificar"
    val bio = currentUser?.bio ?: "Sin biografía disponible."

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                LinearProgressIndicator(
                    progress = { 0.65f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = primaryColor,
                    trackColor = primaryColor.copy(alpha = 0.1f)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Cabecera Premium con Degradado Amazónico
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
                        ),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
                    .padding(bottom = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White)
                        }
                        Text(
                            "Perfil de Voluntario",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Foto de Perfil
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(24.dp).size(60.dp),
                                tint = Color.White
                            )
                        }
                        Surface(
                            modifier = Modifier.size(36.dp).align(Alignment.BottomEnd).offset(x = (-4).dp, y = (-4).dp),
                            shape = CircleShape,
                            color = secondaryColor,
                            shadowElevation = 6.dp
                        ) {
                            Icon(Icons.Outlined.PhotoCamera, null, modifier = Modifier.padding(8.dp).size(18.dp), tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = profileName, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(text = profileEmail, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("Postulaciones", "0")
                        StatDivider()
                        StatItem("Horas", "0h")
                        StatDivider()
                        StatItem("Nivel", "Bronce")
                    }
                }
            }

            if (userDetailState is UserDetailState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Pestañas Estilizadas
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = primaryColor,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = primaryColor,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == index) primaryColor else Color.Gray
                                )
                            }
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    when (selectedTab) {
                        0 -> InfoPersonalContent(
                            birthDate = birthDate,
                            phone = phone,
                            gender = gender,
                            location = location,
                            bio = bio,
                            onEditClick = { showSheetType = SheetType.PERSONAL }
                        )
                        1 -> StudyContent(
                            studies = userStudies,
                            onAddClick = { showSheetType = SheetType.STUDY }
                        )
                        2 -> ExperienceContent(
                            experiences = userExperiences,
                            onAddClick = { showSheetType = SheetType.EXPERIENCE }
                        )
                        3 -> AchievementsContent()
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet - Formulario Elegante
    if (showSheetType != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheetType = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 16.dp,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Box(modifier = Modifier.fillMaxHeight(0.9f)) {
                when (showSheetType) {
                    SheetType.PERSONAL -> PersonalForm(
                        currentName = profileName,
                        currentBirthDate = birthDate,
                        currentPhone = phone,
                        currentGender = gender,
                        currentLocation = location,
                        currentBio = bio,
                        onDismiss = { showSheetType = null },
                        onSave = { n, bd, p, g, l, b ->
                            if (userId != -1) {
                                val updatedUser = currentUser?.copy(
                                    name = n,
                                    birthDate = bd,
                                    phone = p,
                                    gender = g,
                                    location = l,
                                    bio = b
                                ) ?: User(id = userId, name = n, email = profileEmail, birthDate = bd, phone = p, gender = g, location = l, bio = b)
                                
                                userViewModel.updateUser(token, userId, updatedUser)
                                // Actualizar AuthViewModel para que el nombre cambie en el App Bar global
                                authViewModel.updateLocalUserData(n, profileEmail)
                            }
                            showSheetType = null
                        }
                    )
                    SheetType.STUDY -> StudyForm(
                        onDismiss = { showSheetType = null },
                        onSave = { study ->
                            if (userId != -1) {
                                studyViewModel.createStudy(token, study.copy(user_id = userId))
                            }
                            showSheetType = null
                        }
                    )
                    SheetType.EXPERIENCE -> ExperienceForm(
                        onDismiss = { showSheetType = null },
                        onSave = { exp ->
                            if (userId != -1) {
                                experienceViewModel.createExperience(token, exp.copy(user_id = userId))
                            }
                            showSheetType = null
                        }
                    )
                    null -> {}
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
fun StatDivider() {
    Box(
        modifier = Modifier
            .height(30.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

@Composable
fun InfoPersonalContent(
    birthDate: String,
    phone: String,
    gender: String,
    location: String,
    bio: String,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Información Básica",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Editar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard {
            InfoRow(Icons.Default.Cake, "Nacimiento", birthDate)
            InfoRow(Icons.Default.Phone, "Teléfono", phone)
            InfoRow(Icons.Default.PersonOutline, "Género", gender)
            InfoRow(Icons.Default.LocationOn, "Ubicación", location)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Acerca de mí",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Text(
                text = bio,
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StudyContent(studies: List<Study>, onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Formación Académica",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = onAddClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Text("Agregar", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (studies.isEmpty()) {
            EmptyState(Icons.Default.School, "No has agregado estudios aún.")
        } else {
            studies.forEach { study ->
                ItemCard(
                    icon = Icons.Default.School,
                    title = study.institution,
                    subtitle = study.degree,
                    date = "${study.startDate} - ${study.endDate ?: "Presente"}"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ExperienceContent(experiences: List<Experience>, onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Experiencia de Voluntariado",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = onAddClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Text("Agregar", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (experiences.isEmpty()) {
            EmptyState(Icons.Default.Work, "No has agregado experiencias aún.")
        } else {
            experiences.forEach { exp ->
                ItemCard(
                    icon = Icons.Default.Work,
                    title = exp.company,
                    subtitle = exp.position,
                    date = "${exp.startDate} - ${exp.endDate ?: "Presente"}",
                    description = exp.description
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AchievementsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Próximamente",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Tus insignias y reconocimientos aparecerán aquí.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ItemCard(icon: ImageVector, title: String, subtitle: String, date: String, description: String? = null) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Text(date, fontSize = 12.sp, color = Color.Gray)
                if (description != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(description, fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, color = Color.Gray, textAlign = TextAlign.Center)
    }
}

@Composable
fun PersonalForm(
    currentName: String,
    currentBirthDate: String,
    currentPhone: String,
    currentGender: String,
    currentLocation: String,
    currentBio: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var birthDate by remember { mutableStateOf(if (currentBirthDate == "Sin especificar") "" else currentBirthDate) }
    var phone by remember { mutableStateOf(if (currentPhone == "Sin especificar") "" else currentPhone) }
    var gender by remember { mutableStateOf(if (currentGender == "Sin especificar") "" else currentGender) }
    var location by remember { mutableStateOf(if (currentLocation == "Sin especificar") "" else currentLocation) }
    var bio by remember { mutableStateOf(if (currentBio == "Sin biografía disponible.") "" else currentBio) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Editar Información Personal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = birthDate, onValueChange = { birthDate = it }, label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Género") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Biografía") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onSave(name, birthDate, phone, gender, location, bio) }) { Text("Guardar Cambios") }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StudyForm(onDismiss: () -> Unit, onSave: (Study) -> Unit) {
    var institution by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text("Agregar Formación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = institution, onValueChange = { institution = it }, label = { Text("Institución") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Grado / Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Inicio") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Fin (Opcional)") }, modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onSave(Study(institution = institution, degree = degree, fieldOfStudy = "General", startDate = startDate, endDate = endDate.ifEmpty { null }, user_id = 0)) }) { Text("Agregar") }
        }
    }
}

@Composable
fun ExperienceForm(onDismiss: () -> Unit, onSave: (Experience) -> Unit) {
    var organization by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text("Agregar Experiencia", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = organization, onValueChange = { organization = it }, label = { Text("Organización") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Rol") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Inicio") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Fin (Opcional)") }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onSave(Experience(company = organization, position = role, startDate = startDate, endDate = endDate.ifEmpty { null }, description = description, user_id = 0)) }) { Text("Agregar") }
        }
    }
}
