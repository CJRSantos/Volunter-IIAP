package com.gdcj.voluntariadoiiap.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.gdcj.voluntariadoiiap.ui.viewmodel.AuthState
import com.gdcj.voluntariadoiiap.ui.viewmodel.AuthViewModel
import com.gdcj.voluntariadoiiap.ui.viewmodel.OperationState
import com.gdcj.voluntariadoiiap.ui.viewmodel.UserViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onBackClick: () -> Unit,
    onAccountDeleted: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = authViewModel.sessionManager
    val userUid by authViewModel.userUid.collectAsState()
    
    var biometricEnabled by remember { mutableStateOf(sessionManager.isBiometricEnabled()) }
    var twoFactorEnabled by remember { mutableStateOf(false) }
    var publicProfile by remember { mutableStateOf(true) }
    var showDataProcessing by remember { mutableStateOf(true) }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    val authState by authViewModel.authState.collectAsState()
    val userOpState by userViewModel.operationState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            Toast.makeText(context, (authState as AuthState.Success).message, Toast.LENGTH_SHORT).show()
            showChangePasswordDialog = false
            authViewModel.resetAuthState()
        } else if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            authViewModel.resetAuthState()
        }
    }

    LaunchedEffect(userOpState) {
        if (userOpState is OperationState.Success) {
            if ((userOpState as OperationState.Success).message == "Usuario eliminado") {
                Toast.makeText(context, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                authViewModel.logout { onAccountDeleted() }
            }
            userViewModel.resetOperationState()
        } else if (userOpState is OperationState.Error) {
            Toast.makeText(context, (userOpState as OperationState.Error).message, Toast.LENGTH_SHORT).show()
            userViewModel.resetOperationState()
        }
    }

    fun showBiometricPrompt(onSuccess: () -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    (context as FragmentActivity).runOnUiThread { onSuccess() }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Confirma tu identidad para continuar")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Seguridad y Privacidad", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                SecuritySectionTitle("Cuenta y Acceso")
                SecurityItem(
                    icon = Icons.Outlined.Lock,
                    title = "Cambiar Contraseña",
                    subtitle = "Actualiza tu contraseña regularmente",
                    onClick = { showChangePasswordDialog = true }
                )
                SecurityItem(
                    icon = Icons.Outlined.Fingerprint,
                    title = "Acceso Biométrico",
                    subtitle = "Usa tu huella para entrar",
                    trailing = {
                        Switch(
                            checked = biometricEnabled, 
                            onCheckedChange = { checked ->
                                if (checked) {
                                    val biometricManager = BiometricManager.from(context)
                                    when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                                        BiometricManager.BIOMETRIC_SUCCESS -> {
                                            showBiometricPrompt {
                                                biometricEnabled = true
                                                sessionManager.setBiometricEnabled(true)
                                            }
                                        }
                                        else -> {
                                            Toast.makeText(context, "Biometría no disponible", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    biometricEnabled = false
                                    sessionManager.setBiometricEnabled(false)
                                }
                            }
                        )
                    }
                )
                SecurityItem(
                    icon = Icons.Outlined.VerifiedUser,
                    title = "Autenticación de dos pasos",
                    subtitle = "Añade una capa extra de seguridad",
                    trailing = {
                        Switch(checked = twoFactorEnabled, onCheckedChange = { twoFactorEnabled = it })
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                SecuritySectionTitle("Privacidad")
                SecurityItem(
                    icon = Icons.Outlined.Visibility,
                    title = "Perfil Público",
                    subtitle = "Permite que otros vean tus logros",
                    trailing = {
                        Switch(checked = publicProfile, onCheckedChange = { publicProfile = it })
                    }
                )
                SecurityItem(
                    icon = Icons.Outlined.History,
                    title = "Historial de Actividad",
                    subtitle = "Gestiona tus registros pasados",
                    onClick = { /* Ver historial */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                SecuritySectionTitle("Datos Personales")
                SecurityItem(
                    icon = Icons.Outlined.Description,
                    title = "Política de Tratamiento de Datos",
                    subtitle = "Cómo gestionamos tu información",
                    trailing = {
                        Switch(checked = showDataProcessing, onCheckedChange = { showDataProcessing = it })
                    }
                )
                SecurityItem(
                    icon = Icons.Outlined.DeleteForever,
                    title = "Eliminar Cuenta",
                    subtitle = "Esta acción es permanente",
                    iconColor = MaterialTheme.colorScheme.error,
                    labelColor = MaterialTheme.colorScheme.error,
                    onClick = { showDeleteConfirmation = true }
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            isLoading = authState is AuthState.Loading,
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { current, new, confirm ->
                authViewModel.changePassword(current, new, confirm)
            }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("¿Eliminar Cuenta?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción no se puede deshacer. Se borrarán todos tus datos de voluntario de forma permanente.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (userUid.isNotEmpty()) {
                            userViewModel.deleteUser(userUid)
                        } else {
                            Toast.makeText(context, "Error: No se pudo identificar al usuario", Toast.LENGTH_SHORT).show()
                        }
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar definitivamente", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ChangePasswordDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Contraseña", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Contraseña Actual") },
                    visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { currentVisible = !currentVisible }) {
                            Icon(if (currentVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva Contraseña") },
                    visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { newVisible = !newVisible }) {
                            Icon(if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Nueva Contraseña") },
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentPassword, newPassword, confirmPassword) },
                enabled = !isLoading && currentPassword.isNotBlank() && newPassword.isNotBlank() && newPassword == confirmPassword
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SecuritySectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SecurityItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = labelColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (trailing != null) {
                trailing()
            } else if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
