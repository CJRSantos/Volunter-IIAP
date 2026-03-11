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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
    val tabs = listOf("Perfil", "Formación", "Experiencia")
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheetType by remember { mutableStateOf<SheetType?>(null) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        authViewModel.updateProfilePicture(uri)
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
                }
            }
        }
    }

    if (showPhotoOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoOptionsDialog = false },
            title = { Text("Foto de Perfil") },
            text = { Text("Elige una opción para tu foto de perfil.") },
            confirmButton = {
                TextButton(onClick = { showPhotoOptionsDialog = false; launcher.launch("image/*") }) {
                    Text("Cambiar foto")
                }
            },
            dismissButton = {
                if (profilePictureUri != null) {
                    TextButton(onClick = {
                        showPhotoOptionsDialog = false
                        authViewModel.updateProfilePicture(null)
                    }) {
                        Text("Eliminar foto", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                     TextButton(onClick = { showPhotoOptionsDialog = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    if (showSheetType != null) {
        ModalBottomSheet(onDismissRequest = { showSheetType = null }, sheetState = sheetState) {
            Box(modifier = Modifier.fillMaxHeight(0.9f).navigationBarsPadding()) {
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
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
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Detalles Personales", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
        }
        InfoCard {
            InfoRow(Icons.Outlined.Cake, "Nacimiento", user?.birthDate ?: "Sin especificar")
            InfoRow(Icons.Outlined.Phone, "Teléfono", user?.phone ?: "Sin especificar")
            InfoRow(Icons.Outlined.Wc, "Género", user?.gender ?: "Sin especificar")
            InfoRow(Icons.Outlined.LocationOn, "Ubicación", user?.location ?: "Sin especificar")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Acerca de mí", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) {
            Text(text = user?.bio ?: "Sin biografía disponible.", modifier = Modifier.padding(16.dp), fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
}

@Composable
fun StudyContent(studies: List<Study>, onAddClick: () -> Unit, onDelete: (Study) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Formación Académica", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Button(onClick = onAddClick, shape = RoundedCornerShape(12.dp)) { Text("Añadir") }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (studies.isEmpty()) EmptyState(Icons.Default.School, "Sin estudios registrados.")
        else studies.forEach { ItemCard(Icons.Default.School, it.institution, it.degree, "${it.startDate} - ${it.endDate ?: "Presente"}") { onDelete(it) } }
    }
}

@Composable
fun ExperienceContent(experiences: List<Experience>, onAddClick: () -> Unit, onDelete: (Experience) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Experiencia Profesional", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Button(onClick = onAddClick, shape = RoundedCornerShape(12.dp)) { Text("Añadir") }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (experiences.isEmpty()) EmptyState(Icons.Default.Work, "Sin experiencia registrada.")
        else experiences.forEach { ItemCard(Icons.Default.Work, it.company, it.position, "${it.startDate} - ${it.endDate ?: "Presente"}", it.description) { onDelete(it) } }
    }
}

@Composable
fun ItemCard(icon: ImageVector, title: String, subtitle: String, date: String, desc: String? = null, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Text(date, fontSize = 12.sp, color = Color.Gray)
                if (desc != null) Text(desc, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.6f)) }
        }
    }
}

@Composable
fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, msg: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
        Text(msg, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun AchievementsContent() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.EmojiEvents, null, modifier = Modifier.size(60.dp), tint = Color.LightGray)
        Text("Próximamente...", color = Color.Gray)
    }
}

@Composable
fun PersonalForm(state: UserDetailState, onDismiss: () -> Unit, onSave: (User) -> Unit) {
    val u = (state as? UserDetailState.Success)?.user
    var name by remember { mutableStateOf(u?.name ?: "") }
    var bd by remember { mutableStateOf(u?.birthDate ?: "") }
    var phone by remember { mutableStateOf(u?.phone ?: "") }
    var gender by remember { mutableStateOf(u?.gender ?: "") }
    var loc by remember { mutableStateOf(u?.location ?: "") }
    var bio by remember { mutableStateOf(u?.bio ?: "") }

    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Información Personal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = name, 
            onValueChange = { name = it }, 
            label = { Text("Nombre Completo") }, 
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(12.dp)
        )
        
        OutlinedTextField(
            value = bd, 
            onValueChange = { bd = it }, 
            label = { Text("Nacimiento (YYYY-MM-DD)") }, 
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            leadingIcon = { Icon(Icons.Default.Cake, null, tint = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(12.dp)
        )
        
        OutlinedTextField(
            value = phone, 
            onValueChange = { phone = it }, 
            label = { Text("Teléfono") }, 
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            leadingIcon = { Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.primary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp)
        )
        
        OutlinedTextField(
            value = gender, 
            onValueChange = { gender = it }, 
            label = { Text("Género") }, 
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            leadingIcon = { Icon(Icons.Default.Wc, null, tint = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(12.dp)
        )
        
        OutlinedTextField(
            value = loc, 
            onValueChange = { loc = it }, 
            label = { Text("Ubicación") }, 
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(12.dp)
        )
        
        OutlinedTextField(
            value = bio, 
            onValueChange = { bio = it }, 
            label = { Text("Biografía") }, 
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            minLines = 3,
            leadingIcon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { 
                onSave(u?.copy(name = name, birthDate = bd, phone = phone, gender = gender, location = loc, bio = bio) 
                ?: User(id = 0, name = name, email = "", birthDate = bd, phone = phone, gender = gender, location = loc, bio = bio)) 
            }, 
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) { 
            Text("Guardar Cambios", fontWeight = FontWeight.Bold) 
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun StudyForm(onDismiss: () -> Unit, onSave: (Study) -> Unit) {
    var inst by remember { mutableStateOf("") }
    var deg by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Nueva Formación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
        }
        OutlinedTextField(value = inst, onValueChange = { inst = it }, label = { Text("Institución") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), leadingIcon = { Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.primary) }, shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = deg, onValueChange = { deg = it }, label = { Text("Título / Carrera") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), leadingIcon = { Icon(Icons.Default.Book, null, tint = MaterialTheme.colorScheme.primary) }, shape = RoundedCornerShape(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Inicio") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("Fin") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        Button(onClick = { 
            onSave(Study(institution = inst, degree = deg, fieldOfStudy = "General", startDate = start, endDate = end.ifEmpty { null }, user_id = 0)) 
        }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(56.dp), shape = RoundedCornerShape(12.dp)) { Text("Añadir Formación", fontWeight = FontWeight.Bold) }
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Nueva Experiencia", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
        }
        OutlinedTextField(value = comp, onValueChange = { comp = it }, label = { Text("Empresa / Organización") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), leadingIcon = { Icon(Icons.Default.Business, null, tint = MaterialTheme.colorScheme.primary) }, shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = pos, onValueChange = { pos = it }, label = { Text("Cargo / Rol") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), leadingIcon = { Icon(Icons.Default.Badge, null, tint = MaterialTheme.colorScheme.primary) }, shape = RoundedCornerShape(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Inicio") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("Fin") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), minLines = 2, leadingIcon = { Icon(Icons.Default.Description, null, tint = MaterialTheme.colorScheme.primary) }, shape = RoundedCornerShape(12.dp))
        Button(onClick = { 
            onSave(Experience(company = comp, position = pos, startDate = start, endDate = end.ifEmpty { null }, description = desc, user_id = 0)) 
        }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(56.dp), shape = RoundedCornerShape(12.dp)) { Text("Añadir Experiencia", fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
