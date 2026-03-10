package com.gdcj.voluntariadoiiap.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdcj.voluntariadoiiap.R
import com.gdcj.voluntariadoiiap.ui.viewmodel.AuthState
import com.gdcj.voluntariadoiiap.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterClick: (String, String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados de error
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    fun validateInputs(): Boolean {
        var isValid = true
        
        if (name.length < 3) {
            nameError = "El nombre es muy corto"
            isValid = false
        } else nameError = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Correo no válido"
            isValid = false
        } else emailError = null

        if (phone.length < 9) {
            phoneError = "Teléfono inválido"
            isValid = false
        } else phoneError = null

        if (password.length < 6) {
            passError = "Mínimo 6 caracteres"
            isValid = false
        } else passError = null

        return isValid
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_auth),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToLogin) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, null, modifier = Modifier.size(20.dp), tint = Color.White)
                }
                Text("Registro de Voluntario", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                
                Column {
                    Text("¡Únete a nosotros!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text("Completa tus datos personales para continuar.", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }

                ProfessionalField(
                    label = "Nombre y Apellidos",
                    value = name,
                    onValueChange = { name = it },
                    icon = Icons.Default.Person,
                    error = nameError,
                    enabled = authState !is AuthState.Loading
                )

                ProfessionalField(
                    label = "Correo Personal",
                    value = email,
                    onValueChange = { email = it },
                    icon = Icons.Default.Email,
                    error = emailError,
                    enabled = authState !is AuthState.Loading
                )

                ProfessionalField(
                    label = "Número de Celular",
                    value = phone,
                    onValueChange = { phone = it },
                    icon = Icons.Default.PhoneAndroid,
                    error = phoneError,
                    enabled = authState !is AuthState.Loading
                )

                Column {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = passError != null,
                        enabled = authState !is AuthState.Loading,
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.White) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = Color.White)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            errorBorderColor = Color(0xFFE57373)
                        )
                    )
                    if (passError != null) Text(passError!!, color = Color(0xFFE57373), fontSize = 11.sp, modifier = Modifier.padding(start = 12.dp, top = 4.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { 
                        if (validateInputs()) {
                            authViewModel.register(name, email, password, phone) { n, e -> onRegisterClick(n, e) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF38B1C)),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Crear mi cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onBackToLogin) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Ya tienes una cuenta? ", color = Color.White.copy(alpha = 0.8f))
                    Text("Inicia Sesión", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ProfessionalField(label: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, error: String?, enabled: Boolean) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = error != null,
            enabled = enabled,
            leadingIcon = { Icon(icon, null, tint = Color.White) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                errorBorderColor = Color(0xFFE57373)
            )
        )
        if (error != null) Text(error, color = Color(0xFFE57373), fontSize = 11.sp, modifier = Modifier.padding(start = 12.dp, top = 4.dp))
    }
}
