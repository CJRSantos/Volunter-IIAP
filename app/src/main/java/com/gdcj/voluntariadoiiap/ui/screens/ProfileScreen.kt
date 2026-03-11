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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    // Configuración del Recortador de Imagen
    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            authViewModel.updateProfilePicture(result.uriContent)
            Toast.makeText(context, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
        } else {
            val exception = result.error
            exception?.printStackTrace()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val cropOptions = CropImageContractOptions(
                it,
                CropImageOptions(
                    guidelines = CropImageView.Guidelines.ON,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    fixAspectRatio = true,
                    cropShape = CropImageView.CropShape.OVAL,
                    cropMenuCropButtonTitle = "Aceptar"
                )
            )
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
                Button(onClick = { showPhotoOptionsDialog = false; imagePickerLauncher.launch("image/*") }) {
                    Text("Seleccionar Foto")
                }
            },
            dismissButton = {
                Row {
                    if (profilePictureUri != null) {
                        TextButton(onClick = {
                            showPhotoOptionsDialog = false
                            authViewModel.updateProfilePicture(null)
                        }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { showPhotoOptionsDialog = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    if (showSheetType != null) {
        ModalBottomSheet(onDismissRequest = { showSheetType = null }, sheetState = sheetState) {
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
            .padding(bottom = 24.dp)
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
                    modifier = Modifier.size(110.dp),
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
                    Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.padding(6.dp), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = email, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun InfoPersonalContent(state: UserDetailState, onEditClick: () -> Unit) {
    val user = (state as? UserDetailState.Success)?.user
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Información Personal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                
                InfoItem(Icons.Default.Badge, "Nombre Completo", user?.name ?: "No especificado")
                InfoItem(Icons.Default.Email, "Correo Electrónico", user?.email ?: "No especificado")
                InfoItem(Icons.Default.Phone, "Teléfono", user?.phone ?: "No especificado")
                InfoItem(Icons.Default.LocationOn, "Ubicación", user?.location ?: "No especificado")
                InfoItem(Icons.Default.Notes, "Sobre mí", user?.bio ?: "Sin biografía")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, null, modifier = Modifier.size(20.dp).padding(top = 2.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StudyContent(studies: List<Study>, onAddClick: () -> Unit, onDeleteClick: (Study) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Formación Académica", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = onAddClick) { Icon(Icons.Default.AddCircle, null, tint = MaterialTheme.colorScheme.primary) }
        }
        
        if (studies.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                Text("No hay formación registrada", color = Color.Gray)
            }
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
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(Icons.Default.School, null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(study.institution, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${study.degree} en ${study.fieldOfStudy}", fontSize = 13.sp, color = Color.DarkGray)
                Text("${study.startDate} - ${study.endDate ?: "Presente"}", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.LightGray, modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
fun ExperienceContent(experiences: List<Experience>, onAddClick: () -> Unit, onDeleteClick: (Experience) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Experiencia de Voluntariado", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = onAddClick) { Icon(Icons.Default.AddCircle, null, tint = MaterialTheme.colorScheme.primary) }
        }
        
        if (experiences.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                Text("No hay experiencia registrada", color = Color.Gray)
            }
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
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                    Icon(Icons.Default.VolunteerActivism, null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(exp.position, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(exp.company, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    Text("${exp.startDate} - ${exp.endDate ?: "Presente"}", fontSize = 12.sp, color = Color.Gray)
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.LightGray, modifier = Modifier.size(20.dp)) }
            }
            val desc = exp.description
            if (desc != null && desc.isNotEmpty()) Text(desc, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun AchievementsContent() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.EmojiEvents, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tus logros aparecerán aquí", fontWeight = FontWeight.Medium, color = Color.Gray)
        Text("Completa misiones y proyectos para ganar insignias", fontSize = 12.sp, color = Color.LightGray, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalForm(state: UserDetailState, onDismiss: () -> Unit, onSave: (User) -> Unit) {
    val user = (state as? UserDetailState.Success)?.user
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var location by remember { mutableStateOf(user?.location ?: "") }
    var bio by remember { mutableStateOf(user?.bio ?: "") }

    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Editar Información Personal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Sobre mí") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), minLines = 3, shape = RoundedCornerShape(12.dp))
        
        Button(
            onClick = { onSave(User(id = user?.id ?: 0, name = name, email = email, phone = phone, location = location, bio = bio)) }, 
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Guardar Cambios") }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyForm(onDismiss: () -> Unit, onSave: (Study) -> Unit) {
    var inst by remember { mutableStateOf("") }
    var deg by remember { mutableStateOf("") }
    var field by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    
    var degExpanded by remember { mutableStateOf(false) }
    val degOptions = listOf("Bachiller", "Licenciatura", "Maestría", "Doctorado", "Técnico", "Otro")

    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Nueva Formación Académica", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(
            value = inst, 
            onValueChange = { inst = it }, 
            label = { Text("Institución") }, 
            leadingIcon = { Icon(Icons.Default.LocationCity, null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )
        
        ExposedDropdownMenuBox(
            expanded = degExpanded,
            onExpandedChange = { degExpanded = !degExpanded },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            OutlinedTextField(
                value = deg,
                onValueChange = {},
                readOnly = true,
                label = { Text("Grado / Título") },
                leadingIcon = { Icon(Icons.Default.WorkspacePremium, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = degExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = degExpanded,
                onDismissRequest = { degExpanded = false }
            ) {
                degOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            deg = selectionOption
                            degExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = field, 
            onValueChange = { field = it }, 
            label = { Text("Especialidad / Carrera") }, 
            leadingIcon = { Icon(Icons.Default.Subject, null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp)
        )
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            OutlinedTextField(
                value = start, 
                onValueChange = { start = it }, 
                label = { Text("Inicio (YYYY)") }, 
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = end, 
                onValueChange = { end = it }, 
                label = { Text("Fin (Opcional)") }, 
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }
        
        Button(
            onClick = { 
                onSave(Study(institution = inst, degree = deg, fieldOfStudy = field.ifEmpty { "General" }, startDate = start, endDate = end.ifEmpty { null }, user_id = 0)) 
            }, 
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Añadir Formación") }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ExperienceForm(onDismiss: () -> Unit, onSave: (Experience) -> Unit) {
    var comp by remember { mutableStateOf("") }
    var pos by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Nueva Experiencia de Voluntariado", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(
            value = comp, 
            onValueChange = { comp = it }, 
            label = { Text("Organización / Empresa") }, 
            leadingIcon = { Icon(Icons.Default.Business, null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )
        
        OutlinedTextField(
            value = pos, 
            onValueChange = { pos = it }, 
            label = { Text("Cargo / Rol") }, 
            leadingIcon = { Icon(Icons.Default.Work, null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp)
        )
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            OutlinedTextField(
                value = start, 
                onValueChange = { start = it }, 
                label = { Text("Inicio (YYYY-MM)") }, 
                leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = end, 
                onValueChange = { end = it }, 
                label = { Text("Fin (Opcional)") }, 
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }
        
        OutlinedTextField(
            value = desc, 
            onValueChange = { desc = it }, 
            label = { Text("Descripción de actividades") }, 
            leadingIcon = { Icon(Icons.Default.Description, null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            minLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
        
        Button(
            onClick = { 
                onSave(Experience(company = comp, position = pos, startDate = start, endDate = end.ifEmpty { null }, description = desc, user_id = 0)) 
            }, 
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Añadir Experiencia") }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
