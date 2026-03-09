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
    val operationState by userViewModel.operationState.collectAsState()

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

    LaunchedEffect(operationState) {
        if (operationState is OperationState.Success) {
            Toast.makeText(context, (operationState as OperationState.Success).message, Toast.LENGTH_SHORT).show()
            userViewModel.resetOperationState()
            if (userId != -1) {
                userViewModel.fetchUserById(token, userId)
            }
        } else if (operationState is OperationState.Error) {
            Toast.makeText(context, (operationState as OperationState.Error).message, Toast.LENGTH_SHORT).show()
            userViewModel.resetOperationState()
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
                        IconButton(onClick = { /* Settings */ }) {
                            Icon(Icons.Default.Settings, "Configuración", tint = Color.White)
                        }
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
                            name = profileName,
                            birthDate = birthDate,
                            phone = phone,
                            gender = gender,
                            location = location,
                            bio = bio,
                            onEditClick = { showSheetType = SheetType.PERSONAL }
                        )
                        1 -> FormacionContent(
                            studies = userStudies,
                            onAddClick = { showSheetType = SheetType.STUDY },
                            onDelete = { study ->
                                study.id?.let { studyViewModel.deleteStudy(token, it) }
                            }
                        )
                        2 -> ExperienciaContent(
                            experiences = userExperiences,
                            onAddClick = { showSheetType = SheetType.EXPERIENCE },
                            onDelete = { exp ->
                                exp.id?.let { experienceViewModel.deleteExperience(token, it) }
                            }
                        )
                        else -> LogrosContent()
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
                                ) ?: User(id = userId, name = n, email = email, birthDate = bd, phone = p, gender = g, location = l, bio = b)
                                userViewModel.updateUser(token, userId, updatedUser)
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
fun InfoPersonalContent(
    name: String,
    birthDate: String,
    phone: String,
    gender: String,
    location: String,
    bio: String,
    onEditClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        SectionTitle("Información Principal", onEditClick)
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileCard {
            Column(modifier = Modifier.padding(20.dp)) {
                InfoDetailRow(Icons.Outlined.Badge, "Nombre Completo", name)
                InfoDetailRow(Icons.Outlined.Cake, "Fecha de Nacimiento", birthDate)
                InfoDetailRow(Icons.Outlined.Phone, "Teléfono de Contacto", phone)
                InfoDetailRow(Icons.Outlined.Wc, "Género", gender)
                InfoDetailRow(Icons.Outlined.PinDrop, "Ubicación", location)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Bio y Motivación", onEditClick)
        Spacer(modifier = Modifier.height(12.dp))
        ProfileCard {
            Text(
                bio,
                modifier = Modifier.padding(20.dp),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatDivider() {
    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.2f)))
}

@Composable
fun FormacionContent(
    studies: List<Study>,
    onAddClick: () -> Unit,
    onDelete: (Study) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        SectionTitle("Historial Académico", onAddClick, isAdd = true)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (studies.isEmpty()) {
            EmptyStateMessage("¿Tienes estudios?", "Registra tus títulos o cursos para destacar.")
        } else {
            studies.forEach { study ->
                AcademicItem(
                    inst = study.institution,
                    title = study.degree,
                    date = "${study.startDate} - ${study.endDate ?: "Presente"}",
                    isMain = studies.first() == study,
                    onDelete = { onDelete(study) }
                )
            }
        }
    }
}

@Composable
fun ExperienciaContent(
    experiences: List<Experience>,
    onAddClick: () -> Unit,
    onDelete: (Experience) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        SectionTitle("Experiencia Laboral", onAddClick, isAdd = true)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (experiences.isEmpty()) {
            EmptyStateMessage("Agrega tu experiencia", "Incluso voluntariados previos cuentan para tu perfil.")
        } else {
            experiences.forEach { exp ->
                WorkItem(
                    company = exp.company,
                    pos = exp.position,
                    date = "${exp.startDate} - ${exp.endDate ?: "Presente"}",
                    desc = exp.description ?: "",
                    onDelete = { onDelete(exp) }
                )
            }
        }
    }
}

@Composable
fun LogrosContent() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Outlined.EmojiEvents, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tus Insignias", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Pronto podrás ganar insignias por tus horas de voluntariado.", textAlign = TextAlign.Center, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun SectionTitle(title: String, onClick: () -> Unit, isAdd: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        TextButton(onClick = onClick) {
            Icon(if(isAdd) Icons.Default.Add else Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if(isAdd) "Agregar" else "Editar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        content()
    }
}

@Composable
fun InfoDetailRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
            Icon(icon, null, modifier = Modifier.padding(8.dp).size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun AcademicItem(inst: String, title: String, date: String, isMain: Boolean, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if(isMain) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.School, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(inst, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text(date, fontSize = 11.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun WorkItem(company: String, pos: String, date: String, desc: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WorkOutline, null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(company, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(pos, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 18.sp, modifier = Modifier.weight(1f))
                Text(date, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun EmptyStateMessage(title: String, desc: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

// --- FORMULARIOS ---

@OptIn(ExperimentalMaterial3Api::class)
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
    var birthDate by remember { mutableStateOf(currentBirthDate) }
    var phone by remember { mutableStateOf(currentPhone) }
    var gender by remember { mutableStateOf(currentGender) }
    var location by remember { mutableStateOf(currentLocation) }
    var bio by remember { mutableStateOf(currentBio) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        val formatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
                        birthDate = formatter.format(Date(selectedDate))
                    }
                    showDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        FormHeader("Información Personal", "Actualiza tus datos para completar tu perfil de voluntario.")
        
        FormFieldPremium(name, { name = it }, "Nombre Completo", Icons.Outlined.Badge)
        
        // Campo de Fecha de Nacimiento (Calendario)
        OutlinedTextField(
            value = birthDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de Nacimiento") },
            leadingIcon = { Icon(Icons.Outlined.Cake, null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = { 
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Outlined.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { showDatePicker = true },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        FormFieldPremium(phone, { phone = it }, "Teléfono de Contacto", Icons.Outlined.Phone)
        
        // Selector de Género
        var expandedGender by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedGender,
            onExpandedChange = { expandedGender = !expandedGender },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Género") },
                leadingIcon = { Icon(Icons.Outlined.Wc, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            ExposedDropdownMenu(
                expanded = expandedGender,
                onDismissRequest = { expandedGender = false }
            ) {
                listOf("Masculino", "Femenino", "Otro").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            expandedGender = false
                        }
                    )
                }
            }
        }

        FormFieldPremium(location, { location = it }, "Ubicación (Ciudad, Provincia)", Icons.Outlined.PinDrop)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sobre mí / Motivación", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Cuéntanos qué te motiva a ser voluntario...") }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton("Guardar Cambios") {
            onSave(name, birthDate, phone, gender, location, bio)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StudyForm(onDismiss: () -> Unit, onSave: (Study) -> Unit) {
    var institution by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var field by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        FormHeader("Nueva Formación", "Registra tus estudios para validar tus competencias.")
        
        FormFieldPremium(institution, { institution = it }, "Institución Educativa", Icons.Outlined.School)
        FormFieldPremium(degree, { degree = it }, "Grado o Título", Icons.Outlined.HistoryEdu)
        FormFieldPremium(field, { field = it }, "Especialidad", Icons.Outlined.Book)
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                FormFieldPremium(startYear, { startYear = it }, "Año Inicio", Icons.Outlined.CalendarToday)
            }
            Box(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                FormFieldPremium(endYear, { endYear = it }, "Año Fin", Icons.Outlined.CalendarToday)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton("Registrar Estudio", enabled = institution.isNotBlank() && degree.isNotBlank()) {
            onSave(Study(
                institution = institution, 
                degree = degree, 
                fieldOfStudy = field, 
                startDate = startYear, 
                endDate = if(endYear.isBlank()) "Presente" else endYear, 
                user_id = -1 // Se asignará en ProfileScreen
            ))
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ExperienceForm(onDismiss: () -> Unit, onSave: (Experience) -> Unit) {
    var company by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        FormHeader("Nueva Experiencia", "Cuéntanos dónde has trabajado o colaborado anteriormente.")
        
        FormFieldPremium(company, { company = it }, "Empresa / ONG / Institución", Icons.Outlined.Business)
        FormFieldPremium(position, { position = it }, "Cargo ocupado", Icons.Outlined.WorkOutline)
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                FormFieldPremium(startYear, { startYear = it }, "Año Inicio", Icons.Outlined.CalendarToday)
            }
            Box(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                FormFieldPremium(endYear, { endYear = it }, "Año Fin", Icons.Outlined.CalendarToday)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Descripción de funciones", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Breve resumen de tus actividades...") }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton("Registrar Experiencia", enabled = company.isNotBlank() && position.isNotBlank()) {
            onSave(Experience(
                company = company, 
                position = position, 
                description = desc, 
                startDate = startYear, 
                endDate = if(endYear.isBlank()) "Presente" else endYear,
                user_id = -1 // Se asignará en ProfileScreen
            ))
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun FormHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        Text(subtitle, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun FormFieldPremium(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
