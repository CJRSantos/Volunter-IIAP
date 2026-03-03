package com.gdcj.voluntariadoiiap.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdcj.voluntariadoiiap.data.local.SessionManager
import com.gdcj.voluntariadoiiap.data.model.User
import com.gdcj.voluntariadoiiap.ui.viewmodel.OperationState
import com.gdcj.voluntariadoiiap.ui.viewmodel.UserListState
import com.gdcj.voluntariadoiiap.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    name: String,
    email: String,
    userViewModel: UserViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.fetchAuthToken() ?: ""

    val userListState by userViewModel.userListState.collectAsState()
    val operationState by userViewModel.operationState.collectAsState()

    var showUserDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    // Cargar usuarios al entrar
    LaunchedEffect(Unit) {
        userViewModel.fetchUsers(token)
    }

    // Manejar mensajes de éxito/error de operaciones
    LaunchedEffect(operationState) {
        when (operationState) {
            is OperationState.Success -> {
                Toast.makeText(context, (operationState as OperationState.Success).message, Toast.LENGTH_SHORT).show()
                showUserDialog = false
                userViewModel.resetOperationState()
            }
            is OperationState.Error -> {
                Toast.makeText(context, (operationState as OperationState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        selectedUser = null
                        showUserDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir Usuario")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = userListState) {
                is UserListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UserListState.Success -> {
                    UserList(
                        users = state.users,
                        onEditClick = {
                            selectedUser = it
                            showUserDialog = true
                        },
                        onDeleteClick = {
                            it.id?.let { id -> userViewModel.deleteUser(token, id) }
                        }
                    )
                }
                is UserListState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else -> {}
            }
        }
    }

    if (showUserDialog) {
        UserActionDialog(
            user = selectedUser,
            onDismiss = { showUserDialog = false },
            onConfirm = { name, email, roleId ->
                if (selectedUser == null) {
                    userViewModel.createUser(
                        token,
                        User(auth0Id = "manual-id-${System.currentTimeMillis()}", name = name, email = email, role_id = roleId)
                    )
                } else {
                    userViewModel.updateUser(
                        token,
                        selectedUser!!.id!!,
                        selectedUser!!.copy(name = name, email = email, role_id = roleId)
                    )
                }
            }
        )
    }
}

@Composable
fun UserList(
    users: List<User>,
    onEditClick: (User) -> Unit,
    onDeleteClick: (User) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(users) { user ->
            UserCard(user, onEditClick, onDeleteClick)
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onEditClick: (User) -> Unit,
    onDeleteClick: (User) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(user.email, fontSize = 12.sp, color = Color.Gray)
                Text("Rol ID: ${user.role_id}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { onEditClick(user) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray)
            }
            IconButton(onClick = { onDeleteClick(user) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

@Composable
fun UserActionDialog(
    user: User?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var roleId by remember { mutableStateOf(user?.role_id?.toString() ?: "1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (user == null) "Nuevo Usuario" else "Editar Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = roleId, onValueChange = { roleId = it }, label = { Text("Rol ID (1, 2, 3)") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, email, roleId.toIntOrNull() ?: 1) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
