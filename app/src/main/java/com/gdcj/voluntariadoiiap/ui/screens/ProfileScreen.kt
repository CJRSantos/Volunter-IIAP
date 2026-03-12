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
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Subject
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
            userViewModel.fetchUserById(userId)
            userViewModel.fetchUserStudies(userId)
            userViewModel.fetchUserExperiences(userId)
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

    LaunchedEffect(userOpState) { handleState(userOpState, { userViewModel.fetchUserById(userId) }, userViewModel::resetOperationState) }
    LaunchedEffect(studyOpState) { handleState(studyOpState, { userViewModel.fetchUserStudies(userId) }, studyViewModel::resetOperationState) }
    LaunchedEffect(expOpState) { handleState(expOpState, { userViewModel.fetchUserExperiences(userId) }, experienceViewModel::resetOperationState) }

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
                    1 -> StudyContent(userStudies, { showSheetType = SheetType.STUDY }) { study -> study.id?.let { studyViewModel.deleteStudy(it) } }
                    2 -> ExperienceContent(userExperiences, { showSheetType = SheetType.EXPERIENCE }) { exp -> exp.id?.let { experienceViewModel.deleteExperience(it) } }
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
                        userViewModel.updateUser(userId, updatedUser)
                        authViewModel.updateLocalUserData(updatedUser.name, updatedUser.email)
                        showSheetType = null
                    }
                    SheetType.STUDY -> StudyForm(onDismiss = { showSheetType = null }) { study ->
                        studyViewModel.createStudy(study.copy(user_id = userId))
                        showSheetType = null
                    }
                    SheetType.EXPERIENCE -> ExperienceForm(onDismiss = { showSheetType = null }) { exp ->
                        experienceViewModel.createExperience(exp.copy(user_id = userId))
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
                    Icon(Icons.Default.Edit, null, modifier = Modifier.padding(8.dp), tint = Color.White)
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
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun InfoPersonalContent(state: UserDetailState, onEditClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Información Personal", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            FloatingActionButton(onClick = onEditClick, containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Edit, null) }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val user = (state as? UserDetailState.Success)?.user
                InfoItem(Icons.Default.Badge, "Nombre Completo", user?.name ?: "No especificado")
                InfoItem(Icons.Default.Email, "Correo Electrónico", user?.email ?: "No especificado")
                InfoItem(Icons.Default.Phone, "Teléfono", user?.phone ?: "No especificado")
                InfoItem(Icons.Default.LocationOn, "Ubicación", user?.location ?: "No especificado")
                InfoItem(Icons.AutoMirrored.Filled.Notes, "Sobre mí", user?.bio ?: "Sin biografía")
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Icon(icon, null, modifier = Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun StudyContent(studies: List<Study>, onAddClick: () -> Unit, onDeleteClick: (Study) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Formación Académica", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            FloatingActionButton(onClick = onAddClick, containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Add, null) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (studies.isEmpty()) EmptyState("No has agregado estudios aún")
        else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                studies.forEach { study ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(study.degree, fontWeight = FontWeight.Bold)
                                Text("${study.institution} • ${study.startDate}", fontSize = 12.sp)
                            }
                            IconButton(onClick = { onDeleteClick(study) }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExperienceContent(experiences: List<Experience>, onAddClick: () -> Unit, onDeleteClick: (Experience) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Experiencia", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            FloatingActionButton(onClick = onAddClick, containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Add, null) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (experiences.isEmpty()) EmptyState("No has agregado experiencias aún")
        else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                experiences.forEach { exp ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(exp.position, fontWeight = FontWeight.Bold)
                                Text("${exp.company} • ${exp.startDate}", fontSize = 12.sp)
                            }
                            IconButton(onClick = { onDeleteClick(exp) }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsContent() {
    val achievements = listOf(
        Achievement("Primer Voluntariado", Icons.Default.Stars, Color(0xFFFFD700)),
        Achievement("100 Horas Completadas", Icons.Default.Timer, Color(0xFFC0C0C0)),
        Achievement("Héroe de la Selva", Icons.Default.Nature, Color(0xFF4CAF50)),
        Achievement("Compromiso IIAP", Icons.Default.WorkspacePremium, Color(0xFFCD7F32))
    )
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(achievements) { achievement ->
            Card(modifier = Modifier.fillMaxWidth().aspectRatio(1f), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = achievement.color.copy(alpha = 0.15f))) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(achievement.icon, null, modifier = Modifier.size(48.dp), tint = achievement.color)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(achievement.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun PersonalForm(state: UserDetailState, onDismiss: () -> Unit, onSave: (User) -> Unit) {
    val currentUser = (state as? UserDetailState.Success)?.user
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var location by remember { mutableStateOf(currentUser?.location ?: "") }
    var bio by remember { mutableStateOf(currentUser?.bio ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        Text("Editar Información", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        CustomTextField(value = name, onValueChange = { name = it }, label = "Nombre Completo", icon = Icons.Default.Person)
        CustomTextField(value = email, onValueChange = { email = it }, label = "Correo Electrónico", icon = Icons.Default.Email)
        CustomTextField(value = phone, onValueChange = { phone = it }, label = "Teléfono", icon = Icons.Default.Phone)
        CustomTextField(value = location, onValueChange = { location = it }, label = "Ubicación", icon = Icons.Default.LocationOn)
        CustomTextField(value = bio, onValueChange = { bio = it }, label = "Sobre mí", icon = Icons.AutoMirrored.Filled.Notes, isMultiline = true)
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { onSave(User(name = name, email = email, phone = phone, location = location, bio = bio)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Guardar Cambios") }
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
fun StudyForm(onDismiss: () -> Unit, onSave: (Study) -> Unit) {
    var degree by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var fieldOfStudy by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        Text("Agregar Estudio", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        CustomTextField(value = degree, onValueChange = { degree = it }, label = "Título/Carrera", icon = Icons.Default.School)
        CustomTextField(value = institution, onValueChange = { institution = it }, label = "Institución", icon = Icons.Default.Business)
        CustomTextField(value = fieldOfStudy, onValueChange = { fieldOfStudy = it }, label = "Campo de estudio", icon = Icons.AutoMirrored.Filled.Subject)
        CustomTextField(value = startDate, onValueChange = { startDate = it }, label = "Año de inicio (Ej: 2020)", icon = Icons.Default.CalendarToday)
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { onSave(Study(degree = degree, institution = institution, fieldOfStudy = fieldOfStudy, startDate = startDate, user_id = 0)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Añadir") }
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
fun ExperienceForm(onDismiss: () -> Unit, onSave: (Experience) -> Unit) {
    var position by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        Text("Agregar Experiencia", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        CustomTextField(value = position, onValueChange = { position = it }, label = "Cargo", icon = Icons.Default.Work)
        CustomTextField(value = company, onValueChange = { company = it }, label = "Empresa/Organización", icon = Icons.Default.Business)
        CustomTextField(value = startDate, onValueChange = { startDate = it }, label = "Año de inicio (Ej: 2020)", icon = Icons.Default.CalendarToday)
        CustomTextField(value = description, onValueChange = { description = it }, label = "Descripción", icon = Icons.AutoMirrored.Filled.Subject, isMultiline = true)
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { onSave(Experience(position = position, company = company, startDate = startDate, description = description, user_id = 0)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Añadir") }
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, isMultiline: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = !isMultiline,
        minLines = if (isMultiline) 3 else 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
}
