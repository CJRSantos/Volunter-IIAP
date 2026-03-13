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
import androidx.compose.ui.text.style.TextOverflow
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
    val userUid by authViewModel.userUid.collectAsState()
    val profilePictureUri by authViewModel.profilePictureUri.collectAsState()
    
    val userDetailState by userViewModel.userDetailState.collectAsState()
    val userStudies by studyViewModel.studies.collectAsState()
    val userExperiences by experienceViewModel.experiences.collectAsState()
    
    val userOpState by userViewModel.operationState.collectAsState()
    val studyOpState by studyViewModel.operationState.collectAsState()
    val expOpState by experienceViewModel.operationState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Perfil", "Formación", "Experiencia")
    
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

    LaunchedEffect(userUid) {
        if (userUid.isNotEmpty()) {
            userViewModel.fetchUserById(userUid)
            studyViewModel.fetchUserStudies(userUid)
            experienceViewModel.fetchUserExperiences(userUid)
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

    LaunchedEffect(userOpState) { handleState(userOpState, { if(userUid.isNotEmpty()) userViewModel.fetchUserById(userUid) }, userViewModel::resetOperationState) }
    LaunchedEffect(studyOpState) { handleState(studyOpState, { if(userUid.isNotEmpty()) studyViewModel.fetchUserStudies(userUid) }, studyViewModel::resetOperationState) }
    LaunchedEffect(expOpState) { handleState(expOpState, { if(userUid.isNotEmpty()) experienceViewModel.fetchUserExperiences(userUid) }, experienceViewModel::resetOperationState) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            val user = (userDetailState as? UserDetailState.Success)?.user
            ProfileHeader(
                user?.name ?: name, 
                user?.email ?: email, 
                user?.location ?: "Sin ubicación",
                profilePictureUri, 
                onBackClick
            ) { showPhotoOptionsDialog = true }
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF2E7D32),
                indicator = { tabPositions -> TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = Color(0xFF2E7D32), height = 3.dp) },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(text = title, fontSize = 14.sp, fontWeight = if(selectedTab == index) FontWeight.Bold else FontWeight.Medium) })
                }
            }

            Box(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
                when (selectedTab) {
                    0 -> InfoPersonalContent(userDetailState, name, email) { showSheetType = SheetType.PERSONAL }
                    1 -> StudyContent(userStudies, { showSheetType = SheetType.STUDY }) { study -> study.id?.let { studyViewModel.deleteStudy(it, userUid) } }
                    2 -> ExperienceContent(userExperiences, { showSheetType = SheetType.EXPERIENCE }) { exp -> exp.id?.let { experienceViewModel.deleteExperience(it, userUid) } }
                }
            }
            
            Button(
                onClick = { showSheetType = SheetType.PERSONAL },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Icon(Icons.Default.Edit, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar mi Perfil", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        ModalBottomSheet(
            onDismissRequest = { showSheetType = null }, 
            sheetState = sheetState, 
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Box(modifier = Modifier.fillMaxHeight(0.9f).navigationBarsPadding()) {
                when (showSheetType) {
                    SheetType.PERSONAL -> PersonalForm(userDetailState, name, email, onDismiss = { showSheetType = null }) { updatedUser ->
                        if (userUid.isNotEmpty()) {
                            userViewModel.updateUserInFirebase(userUid, updatedUser)
                            authViewModel.updateLocalUserData(updatedUser.name, updatedUser.email)
                        }
                        showSheetType = null
                    }
                    SheetType.STUDY -> StudyForm(onDismiss = { showSheetType = null }) { study ->
                        if (userUid.isNotEmpty()) {
                            studyViewModel.createStudy(userUid, study)
                        }
                        showSheetType = null
                    }
                    SheetType.EXPERIENCE -> ExperienceForm(onDismiss = { showSheetType = null }) { exp ->
                        if (userUid.isNotEmpty()) {
                            experienceViewModel.createExperience(userUid, exp)
                        }
                        showSheetType = null
                    }
                    null -> {}
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(name: String, email: String, location: String, profilePictureUri: Uri?, onBackClick: () -> Unit, onPhotoClick: () -> Unit) {
    val greenGradient = Brush.verticalGradient(colors = listOf(Color(0xFF2E7D32), Color(0xFF43A047)))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = greenGradient)
            .padding(bottom = 60.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(onClick = onBackClick, shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White, modifier = Modifier.padding(8.dp))
                }
                Text("Perfil de Voluntario", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(48.dp)) // Espacio vacío para balancear el título
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(120.dp).clickable { onPhotoClick() }) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.3f),
                    border = BorderStroke(4.dp, Color.White)
                ) {
                    if (profilePictureUri != null) {
                        AsyncImage(model = profilePictureUri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.Person, null, modifier = Modifier.padding(30.dp), tint = Color.White)
                    }
                }
                Surface(modifier = Modifier.size(32.dp).offset(x = (-4).dp, y = (-4).dp), shape = CircleShape, color = Color(0xFF8BC34A), border = BorderStroke(2.dp, Color.White)) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.padding(6.dp), tint = Color.White)
                }
            }
        }
    }

    // Floating Info Card
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-40).dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1B5E20), textAlign = TextAlign.Center)
            Text(text = email, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = location, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
            }
        }
    }
}

@Composable
fun InfoPersonalContent(state: UserDetailState, defaultName: String, defaultEmail: String, onEditClick: () -> Unit) {
    val user = (state as? UserDetailState.Success)?.user
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sobre mí", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = user?.bio ?: "Completa tu biografía para que otros te conozcan mejor.",
            fontSize = 15.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        ProfileDataItem(Icons.Default.Badge, "NOMBRE COMPLETO", user?.name ?: defaultName)
        ProfileDataItem(Icons.Default.Email, "CORREO ELECTRÓNICO", user?.email ?: defaultEmail)
        ProfileDataItem(Icons.Default.Phone, "TELÉFONO", user?.phone ?: "No especificado")
        ProfileDataItem(Icons.Default.LocationOn, "UBICACIÓN", user?.location ?: "No especificado")
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileDataItem(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9).copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = Color.White) {
                Icon(icon, null, modifier = Modifier.padding(10.dp), tint = Color(0xFF2E7D32))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
            }
        }
    }
}

@Composable
fun StudyContent(studies: List<Study>, onAddClick: () -> Unit, onDeleteClick: (Study) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Formación Académica", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            FloatingActionButton(onClick = onAddClick, containerColor = Color(0xFF2E7D32), contentColor = Color.White, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Add, null) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (studies.isEmpty()) EmptyState("No has agregado estudios aún")
        else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                studies.forEach { study ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(study.degree, fontWeight = FontWeight.Bold)
                                Text("${study.institution} • ${study.startDate}", fontSize = 12.sp)
                            }
                            IconButton(onClick = { onDeleteClick(study) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
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
            Text("Experiencia", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            FloatingActionButton(onClick = onAddClick, containerColor = Color(0xFF2E7D32), contentColor = Color.White, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Add, null) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (experiences.isEmpty()) EmptyState("No has agregado experiencias aún")
        else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                experiences.forEach { exp ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(exp.position, fontWeight = FontWeight.Bold)
                                Text("${exp.company} • ${exp.startDate}", fontSize = 12.sp)
                            }
                            IconButton(onClick = { onDeleteClick(exp) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Gray, textAlign = TextAlign.Center)
    }
}

@Composable
fun PersonalForm(state: UserDetailState, defaultName: String, defaultEmail: String, onDismiss: () -> Unit, onSave: (User) -> Unit) {
    val currentUser = (state as? UserDetailState.Success)?.user
    var name by remember { mutableStateOf(currentUser?.name ?: defaultName) }
    var email by remember { mutableStateOf(currentUser?.email ?: defaultEmail) }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var location by remember { mutableStateOf(currentUser?.location ?: "") }
    var bio by remember { mutableStateOf(currentUser?.bio ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Editar Perfil", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF1B5E20))
        Spacer(modifier = Modifier.height(24.dp))
        
        CustomTextField(value = name, onValueChange = { name = it }, label = "Nombre Completo", icon = Icons.Default.Person)
        CustomTextField(value = email, onValueChange = { email = it }, label = "Correo Electrónico", icon = Icons.Default.Email)
        
        CustomTextField(
            value = phone, 
            onValueChange = { if (it.length <= 9 && it.all { c -> c.isDigit() }) phone = it }, 
            label = "Teléfono", 
            icon = Icons.Default.Phone, 
            keyboardType = KeyboardType.Number
        )
        
        CustomTextField(value = location, onValueChange = { location = it }, label = "Ubicación", icon = Icons.Default.LocationOn)
        CustomTextField(value = bio, onValueChange = { bio = it }, label = "Sobre mí", icon = Icons.AutoMirrored.Filled.Notes, isMultiline = true)
        
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { onSave(User(name = name, email = email, phone = phone, location = location, bio = bio)) }, 
            modifier = Modifier.fillMaxWidth().height(56.dp), 
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) { Text("Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancelar", color = Color.Gray) }
        Spacer(modifier = Modifier.height(24.dp))
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
        CustomTextField(value = startDate, onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) startDate = it }, label = "Año de inicio (Ej: 2020)", icon = Icons.Default.CalendarToday, keyboardType = KeyboardType.Number)
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { onSave(Study(degree = degree, institution = institution, fieldOfStudy = fieldOfStudy, startDate = startDate)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Añadir") }
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
        CustomTextField(value = startDate, onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) startDate = it }, label = "Año de inicio (Ej: 2020)", icon = Icons.Default.CalendarToday, keyboardType = KeyboardType.Number)
        CustomTextField(value = description, onValueChange = { description = it }, label = "Descripción", icon = Icons.AutoMirrored.Filled.Subject, isMultiline = true)
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { onSave(Experience(position = position, company = company, startDate = startDate, description = description)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Añadir") }
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
fun CustomTextField(
    value: String, 
    onValueChange: (String) -> Unit, 
    label: String, 
    icon: ImageVector, 
    isMultiline: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = Color(0xFF2E7D32)) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = !isMultiline,
        minLines = if (isMultiline) 3 else 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2E7D32),
            focusedLabelColor = Color(0xFF2E7D32)
        )
    )
}
