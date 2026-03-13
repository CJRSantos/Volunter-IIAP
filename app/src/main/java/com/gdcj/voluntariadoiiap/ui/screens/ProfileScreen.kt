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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.gdcj.voluntariadoiiap.data.model.User
import com.gdcj.voluntariadoiiap.ui.viewmodel.*

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
    val user = (userDetailState as? UserDetailState.Success)?.user

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPersonalSheet by remember { mutableStateOf(false) }
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
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        // Banner Versión Beta
        BetaBanner()

        // Header con "Mi Perfil" y Settings
        ProfileTopBar(onBackClick)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Imagen de Perfil con Badge
                ProfileImageSection(profilePictureUri) { showPhotoOptionsDialog = true }

                Spacer(modifier = Modifier.height(20.dp))

                // Nombre
                Text(
                    text = if (user?.name?.isNotEmpty() == true) user.name else if (name.isEmpty()) "Usuario IIAP" else name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                
                // Rango / Tipo de Voluntariado (Dinámico)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.WorkspacePremium, 
                        contentDescription = null, 
                        tint = Color(0xFFF07D44), 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = user?.volunteerType ?: "Asignar Tipo",
                        fontSize = 20.sp,
                        color = Color(0xFFF07D44),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Etiqueta Entorno de Pruebas
                Surface(
                    color = Color(0xFFE9F0F7),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "ENTORNO DE PRUEBAS",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A6572)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Editar Perfil
                Button(
                    onClick = { showPersonalSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07D44)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Sección Próximamente
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Próximamente",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    Surface(
                        color = Color(0xFFFFEAD1),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "ROADMAP",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B4513)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lista de items de Próximamente
            items(upcomingFeatures) { feature ->
                UpcomingFeatureItem(feature)
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Estamos trabajando para integrar estas funcionalidades.\nTu perfil se actualizará automáticamente cuando estén disponibles.",
                    modifier = Modifier.padding(horizontal = 48.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray.copy(alpha = 0.8f),
                    lineHeight = 20.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
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

    if (showPersonalSheet) {
        ModalBottomSheet(onDismissRequest = { showPersonalSheet = false }, sheetState = sheetState) {
            PersonalForm(userDetailState, name, email, onDismiss = { showPersonalSheet = false }) { updatedUser ->
                if (userUid.isNotEmpty()) {
                    userViewModel.updateUserInFirebase(userUid, updatedUser)
                    authViewModel.updateLocalUserData(updatedUser.name, updatedUser.email)
                }
                showPersonalSheet = false
            }
        }
    }
}

@Composable
fun BetaBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDE8D7))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Info, 
            contentDescription = null, 
            tint = Color(0xFFD47321), 
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "VERSIÓN BETA",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD47321)
            )
            Text(
                text = "Este es un entorno de pruebas. Los datos mostrados no están sincronizados con el servidor real.",
                fontSize = 12.sp,
                color = Color(0xFF6B4B3A),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun ProfileTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Mi Perfil",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1C1E)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = Color(0xFFE9EEF1)
            ) {
                IconButton(onClick = { /* Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color(0xFF43474E), modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun ProfileImageSection(uri: Uri?, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        // Círculo decorativo naranja
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color(0xFFF07D44).copy(alpha = 0.15f), CircleShape)
        )

        // Foto de Perfil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.BottomEnd
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                border = BorderStroke(4.dp, Color(0xFFF07D44)),
                color = Color.White
            ) {
                if (uri != null) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person, 
                        contentDescription = null, 
                        modifier = Modifier.padding(24.dp), 
                        tint = Color.LightGray
                    )
                }
            }
            
            // Verification Badge (checkmark icon inside an orange circle)
            Surface(
                modifier = Modifier.size(34.dp).offset(x = (-4).dp, y = (-4).dp),
                shape = CircleShape,
                color = Color(0xFFF07D44),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Icon(
                    Icons.Default.Verified, 
                    contentDescription = "Verificado", 
                    modifier = Modifier.padding(6.dp), 
                    tint = Color.White
                )
            }
        }
    }
}

data class UpcomingFeature(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

val upcomingFeatures = listOf(
    UpcomingFeature(
        "Seguimiento de Impacto",
        "Visualiza tu contribución en tiempo real con métricas avanzadas.",
        Icons.Default.BarChart,
        Color(0xFF4285F4)
    ),
    UpcomingFeature(
        "Historial Certificado",
        "Descarga certificados oficiales de tus horas de voluntariado.",
        Icons.Default.CardMembership,
        Color(0xFF34A853)
    ),
    UpcomingFeature(
        "Mapa de Iniciativas",
        "Explora proyectos activos en la Amazonía a través de un mapa interactivo.",
        Icons.Default.Map,
        Color(0xFFFBBC05)
    )
)

@Composable
fun UpcomingFeatureItem(feature: UpcomingFeature) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = feature.color.copy(alpha = 0.1f)
            ) {
                Icon(
                    feature.icon, 
                    contentDescription = null, 
                    modifier = Modifier.padding(12.dp), 
                    tint = feature.color
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = feature.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Text(
                    text = feature.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
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
    var volunteerType by remember { mutableStateOf(currentUser?.volunteerType ?: "Voluntario Senior") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        Text("Editar Información", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        CustomTextField(value = name, onValueChange = { name = it }, label = "Nombre Completo", icon = Icons.Default.Person)
        CustomTextField(value = email, onValueChange = { email = it }, label = "Correo Electrónico", icon = Icons.Default.Email)
        CustomTextField(value = phone, onValueChange = { phone = it }, label = "Teléfono", icon = Icons.Default.Phone)
        CustomTextField(value = location, onValueChange = { location = it }, label = "Ubicación", icon = Icons.Default.LocationOn)
        
        // Selector de Tipo de Voluntariado
        Text("Categoría de Voluntario", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
        val volunteerOptions = listOf("Voluntario Junior", "Voluntario Senior", "Especialista", "Investigador")
        var expanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = volunteerType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Voluntariado") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.WorkspacePremium, null, tint = MaterialTheme.colorScheme.primary) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                volunteerOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            volunteerType = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        CustomTextField(value = bio, onValueChange = { bio = it }, label = "Sobre mí", icon = Icons.Default.Description, isMultiline = true)
        
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { onSave(User(name = name, email = email, phone = phone, location = location, bio = bio, volunteerType = volunteerType)) }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(12.dp)
        ) { 
            Text("Guardar Cambios") 
        }
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
