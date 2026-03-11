package com.gdcj.voluntariadoiiap.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.gdcj.voluntariadoiiap.data.model.Experience
import com.gdcj.voluntariadoiiap.data.model.Study
import com.gdcj.voluntariadoiiap.data.model.User
import com.gdcj.voluntariadoiiap.ui.viewmodel.*

enum class SheetType { PERSONAL, STUDY, EXPERIENCE }
data class Achievement(val title: String, val icon: ImageVector, val color: Color)

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
    val userId by authViewModel.userId.collectAsState()
    val profilePictureUri by authViewModel.profilePictureUri.collectAsState()
    
    val userDetailState by userViewModel.userDetailState.collectAsState()
    val userStudies by userViewModel.userStudies.collectAsState()
    val userExperiences by userViewModel.userExperiences.collectAsState()
    
    val userOpState by userViewModel.operationState.collectAsState()
    val studyOpState by studyViewModel.operationState.collectAsState()
    val expOpState by experienceViewModel.operationState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Perfil", "Formación", "Experiencia", "Logros")
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheetType by remember { mutableStateOf<SheetType?>(null) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            authViewModel.updateProfilePicture(result.uriContent)
            Toast.makeText(context, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val cropOptions = CropImageContractOptions(it, CropImageOptions(
                guidelines = CropImageView.Guidelines.ON,
                aspectRatioX = 1, aspectRatioY = 1, fixAspectRatio = true,
                cropShape = CropImageView.CropShape.OVAL, cropMenuCropButtonTitle = "Aceptar"
            ))
            imageCropLauncher.launch(cropOptions)
        }
    }

    LaunchedEffect(userId) {
        if (userId != -1) {
            userViewModel.fetchUserById(token, userId)
            userViewModel.fetchUserStudies(token, userId)
            userViewModel.fetchUserExperiences(token, userId)
        }
    }

    fun handleState(state: OperationState, onRefresh: () -> Unit, onReset: () -> Unit) {
        if (state is OperationState.Success) {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            onRefresh()
            onReset()
        } else if (state is OperationState.Error) {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            onReset()
        }
    }

    LaunchedEffect(userOpState) { handleState(userOpState, { userViewModel.fetchUserById(token, userId) }, userViewModel::resetOperationState) }
    LaunchedEffect(studyOpState) { handleState(studyOpState, { userViewModel.fetchUserStudies(token, userId) }, studyViewModel::resetOperationState) }
    LaunchedEffect(expOpState) { handleState(expOpState, { userViewModel.fetchUserExperiences(token, userId) }, experienceViewModel::resetOperationState) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProfileHeader(name, email, profilePictureUri, onBackClick) { showPhotoOptionsDialog = true }
            
            ImpactSummary()

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions -> TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = MaterialTheme.colorScheme.primary, height = 3.dp) },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(text = title, fontSize = 13.sp, fontWeight = if(selectedTab == index) FontWeight.Bold else FontWeight.Medium) })
                }
            }

            Box(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
                when (selectedTab) {
                    0 -> InfoPersonalContent(userDetailState) { showSheetType = SheetType.PERSONAL }
                    1 -> StudyContent(userStudies, { showSheetType = SheetType.STUDY }) { study -> study.id?.let { studyViewModel.deleteStudy(token, it) } }
                    2 -> ExperienceContent(userExperiences, { showSheetType = SheetType.EXPERIENCE }) { exp -> exp.id?.let { experienceViewModel.deleteExperience(token, it) } }
                    3 -> AchievementsContent()
                }
            }
        }
    }

    if (showPhotoOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoOptionsDialog = false },
            title = { Text("Foto de Perfil") },
            text = { Text("¿Deseas cambiar o eliminar tu foto de perfil?") },
            confirmButton = {
                Button(onClick = { showPhotoOptionsDialog = false; imagePickerLauncher.launch("image/*") }) { Text("Seleccionar Foto") }
            },
            dismissButton = {
                Row {
                    if (profilePictureUri != null) {
                        TextButton(onClick = { showPhotoOptionsDialog = false; authViewModel.updateProfilePicture(null) }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { showPhotoOptionsDialog = false }) { Text("Cancelar") }
                }
            }
        )
    }

    if (showSheetType != null) {
        ModalBottomSheet(onDismissRequest = { showSheetType = null }, sheetState = sheetState, dragHandle = { BottomSheetDefaults.DragHandle() }) {
            Box(modifier = Modifier.fillMaxHeight(0.85f).navigationBarsPadding()) {
                when (showSheetType) {
                    SheetType.PERSONAL -> PersonalForm(userDetailState, onDismiss = { showSheetType = null }) { updatedUser ->
                        userViewModel.updateUser(token, userId, updatedUser)
                        authViewModel.updateLocalUserData(updatedUser.name, updatedUser.email)
                        showSheetType = null
                    }
                    SheetType.STUDY -> StudyForm(onDismiss = { showSheetType = null }) { study ->
                        studyViewModel.createStudy(token, study.copy(user_id = userId))
                        showSheetType = null
                    }
                    SheetType.EXPERIENCE -> ExperienceForm(onDismiss = { showSheetType = null }) { exp ->
                        experienceViewModel.createExperience(token, exp.copy(user_id = userId))
                        showSheetType = null
                    }
                    null -> {}
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(name: String, email: String, profilePictureUri: Uri?, onBackClick: () -> Unit, onPhotoClick: () -> Unit) {
    val brandColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(colors = listOf(brandColor, brandColor.copy(alpha = 0.8f))), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .padding(bottom = 16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White) }
                Text("Mi Perfil de Voluntario", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(modifier = Modifier.size(48.dp))
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { onPhotoClick() }) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    border = BorderStroke(3.dp, Color.White)
                ) {
                    if (profilePictureUri != null) {
                        AsyncImage(model = profilePictureUri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.Person, null, modifier = Modifier.padding(24.dp), tint = Color.White)
                    }
                }
                Surface(modifier = Modifier.size(32.dp).align(Alignment.BottomEnd), shape = CircleShape, color = MaterialTheme.colorScheme.secondary, shadowElevation = 4.dp) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.padding(8.dp), tint = Color.White) // CAMBIADO A ICONO DE LÁPIZ
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = email, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun ImpactSummary() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ImpactItem(Icons.Default.AccessTime, "120h", "Horas", Modifier.weight(1f))
        ImpactItem(Icons.Default.TaskAlt, "8", "Proyectos", Modifier.weight(1f))
        ImpactItem(Icons.Default.Groups, "500+", "Impacto", Modifier.weight(1f))
    }
}

@Composable
fun ImpactItem(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun InfoPersonalContent(state: UserDetailState, onEditClick: () -> Unit) {
    val user = (state as? UserDetailState.Success)?.user
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Información Personal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onEditClick, modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).size(36.dp)) { 
                        Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                
                InfoItem(Icons.Default.Badge, "Nombre Completo", user?.name ?: "No especificado")
                InfoItem(Icons.Default.Email, "Correo Electrónico", user?.email ?: "No especificado")
                InfoItem(Icons.Default.Phone, "Teléfono", user?.phone ?: "No especificado")
                InfoItem(Icons.Default.LocationOn, "Ubicación", user?.location ?: "No especificado")
                InfoItem(Icons.Default.Notes, "Sobre mí", user?.bio ?: "Sin biografía")
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.Top) {
        Surface(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)) {
            Icon(icon, null, modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.secondary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun StudyContent(studies: List<Study>, onAddClick: () -> Unit, onDeleteClick: (Study) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Formación Académica", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(onClick = onAddClick, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Añadir", fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (studies.isEmpty()) {
            EmptyState(Icons.Default.School, "No hay formación registrada")
        } else {
            studies.forEach { study ->
                StudyItem(study) { onDeleteClick(study) }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun StudyItem(study: Study, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(Icons.Default.School, null, modifier = Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(study.institution, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${study.degree} en ${study.fieldOfStudy}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${study.startDate} - ${study.endDate ?: "Presente"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)) }
        }
    }
}

@Composable
fun ExperienceContent(experiences: List<Experience>, onAddClick: () -> Unit, onDeleteClick: (Experience) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Experiencia", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(onClick = onAddClick, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Añadir")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (experiences.isEmpty()) {
            EmptyState(Icons.Default.VolunteerActivism, "No hay experiencia registrada")
        } else {
            experiences.forEach { exp ->
                ExperienceItem(exp) { onDeleteClick(exp) }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ExperienceItem(exp: Experience, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                    Icon(Icons.Default.VolunteerActivism, null, modifier = Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(exp.position, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(exp.company, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Text("${exp.startDate} - ${exp.endDate ?: "Presente"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)) }
            }
            if (!exp.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(exp.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, message: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun PersonalForm(state: UserDetailState, onDismiss: () -> Unit, onSave: (User) -> Unit) {
    val user = (state as? UserDetailState.Success)?.user
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var location by remember { mutableStateOf(user?.location ?: "") }
    var bio by remember { mutableStateOf(user?.bio ?: "") }

    Column(modifier = Modifier.padding(bottom = 24.dp).verticalScroll(rememberScrollState())) {
        FormHeader(title = "Editar Información", onDismiss = onDismiss)
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            CustomTextField(value = name, onValueChange = { name = it }, label = "Nombre Completo", icon = Icons.Default.Person)
            CustomTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
            
            // VALIDACIÓN TELÉFONO: solo números y máximo 9 dígitos
            CustomTextField(
                value = phone, 
                onValueChange = { newValue ->
                    if (newValue.length <= 9 && newValue.all { it.isDigit() }) {
                        phone = newValue
                    }
                }, 
                label = "Teléfono (9 dígitos)", 
                icon = Icons.Default.Phone, 
                keyboardType = KeyboardType.Number
            )
            
            CustomTextField(value = location, onValueChange = { location = it }, label = "Ubicación", icon = Icons.Default.LocationOn)
            CustomTextField(value = bio, onValueChange = { bio = it }, label = "Sobre mí", icon = Icons.Default.Notes, isMultiline = true)
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(text = "Guardar Cambios", icon = Icons.Default.Save) { onSave(User(id = user?.id ?: 0, name = name, email = email, phone = phone, location = location, bio = bio)) }
        }
    }
}

@Composable
fun StudyForm(onDismiss: () -> Unit, onSave: (Study) -> Unit) {
    var inst by remember { mutableStateOf("") }
    var deg by remember { mutableStateOf("") }
    var field by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var degExpanded by remember { mutableStateOf(false) }
    val degOptions = listOf("Bachiller", "Licenciatura", "Maestría", "Doctorado", "Técnico", "Otro")

    Column(modifier = Modifier.padding(bottom = 24.dp).verticalScroll(rememberScrollState())) {
        FormHeader(title = "Añadir Formación", onDismiss = onDismiss)
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            CustomTextField(value = inst, onValueChange = { inst = it }, label = "Institución", icon = Icons.Default.AccountBalance)
            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                OutlinedTextField(
                    value = deg, onValueChange = {}, readOnly = true, label = { Text("Grado / Título") },
                    leadingIcon = { 
                        Surface(modifier = Modifier.padding(start = 8.dp).size(36.dp), shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                            Icon(Icons.Default.WorkspacePremium, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp)) 
                        }
                    },
                    trailingIcon = { IconButton(onClick = { degExpanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                    modifier = Modifier.fillMaxWidth().clickable { degExpanded = true }, shape = RoundedCornerShape(16.dp)
                )
                DropdownMenu(expanded = degExpanded, onDismissRequest = { degExpanded = false }, modifier = Modifier.fillMaxWidth(0.8f)) {
                    degOptions.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { deg = it; degExpanded = false }) }
                }
            }
            CustomTextField(value = field, onValueChange = { field = it }, label = "Especialidad", icon = Icons.Default.Subject)
            Row {
                Box(modifier = Modifier.weight(1f)) { CustomTextField(value = start, onValueChange = { start = it }, label = "Inicio (Año)", icon = Icons.Default.CalendarToday) }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) { CustomTextField(value = end, onValueChange = { end = it }, label = "Fin", icon = Icons.Default.EventAvailable) }
            }
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(text = "Añadir Formación", icon = Icons.Default.CheckCircle) { onSave(Study(institution = inst, degree = deg, fieldOfStudy = field, startDate = start, endDate = end.ifEmpty { null }, user_id = 0)) }
        }
    }
}

@Composable
fun ExperienceForm(onDismiss: () -> Unit, onSave: (Experience) -> Unit) {
    var comp by remember { mutableStateOf("") }
    var pos by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(bottom = 24.dp).verticalScroll(rememberScrollState())) {
        FormHeader(title = "Nueva Experiencia", onDismiss = onDismiss)
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            CustomTextField(value = comp, onValueChange = { comp = it }, label = "Organización", icon = Icons.Default.Business)
            CustomTextField(value = pos, onValueChange = { pos = it }, label = "Cargo / Rol", icon = Icons.Default.WorkOutline)
            Row {
                Box(modifier = Modifier.weight(1f)) { CustomTextField(value = start, onValueChange = { start = it }, label = "Inicio", icon = Icons.Default.Today) }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) { CustomTextField(value = end, onValueChange = { end = it }, label = "Fin", icon = Icons.Default.Done) }
            }
            CustomTextField(value = desc, onValueChange = { desc = it }, label = "Descripción", icon = Icons.Default.Description, isMultiline = true)
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(text = "Guardar Experiencia", icon = Icons.Default.Save) { onSave(Experience(company = comp, position = pos, startDate = start, endDate = end.ifEmpty { null }, description = desc, user_id = 0)) }
        }
    }
}

@Composable
fun FormHeader(title: String, onDismiss: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        IconButton(onClick = onDismiss, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) { Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp)) }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, keyboardType: KeyboardType = KeyboardType.Text, isMultiline: Boolean = false) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label, fontSize = 14.sp) },
        leadingIcon = { 
            // ICONO NOTABLE CON CONTENEDOR
            Surface(
                modifier = Modifier.padding(start = 8.dp).size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
            }
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), 
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = if (isMultiline) ImeAction.Default else ImeAction.Next),
        singleLine = !isMultiline,
        minLines = if (isMultiline) 3 else 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary, 
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), 
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        )
    )
}

@Composable
fun PrimaryButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)) {
        Icon(icon, null, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(10.dp)); Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun AchievementsContent() {
    val achievements = listOf(
        Achievement("Pionero", Icons.Default.Stars, Color(0xFFFFD700)),
        Achievement("Eco-Guerrero", Icons.Default.Eco, Color(0xFF4CAF50)),
        Achievement("Mentor", Icons.Default.RecordVoiceOver, Color(0xFF2196F3)),
        Achievement("Colaborador Oro", Icons.Default.MilitaryTech, Color(0xFFFF9800)),
        Achievement("Solidario", Icons.Default.Favorite, Color(0xFFE91E63)),
        Achievement("Explorador", Icons.Default.Explore, Color(0xFF9C27B0))
    )
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tus Logros Obtenidos", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(3), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(achievements) { AchievementCard(it) }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = achievement.color.copy(alpha = 0.15f), border = BorderStroke(2.dp, achievement.color)) {
            Icon(achievement.icon, null, modifier = Modifier.padding(20.dp), tint = achievement.color)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(achievement.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}
