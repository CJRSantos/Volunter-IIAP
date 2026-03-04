package com.gdcj.voluntariadoiiap.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.gdcj.voluntariadoiiap.data.model.Area
import com.gdcj.voluntariadoiiap.data.model.Project
import com.gdcj.voluntariadoiiap.data.model.Role
import com.gdcj.voluntariadoiiap.data.model.User
import com.gdcj.voluntariadoiiap.ui.viewmodel.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    name: String,
    email: String,
    userViewModel: UserViewModel,
    roleViewModel: RoleViewModel,
    areaViewModel: AreaViewModel,
    projectViewModel: ProjectViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.fetchAuthToken() ?: ""

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Usuarios", "Roles", "Áreas", "Proyectos")

    val userListState by userViewModel.userListState.collectAsState()
    val roleListState by roleViewModel.roleListState.collectAsState()
    val areaListState by areaViewModel.areaListState.collectAsState()
    val projectListState by projectViewModel.projectListState.collectAsState()

    val userOpState by userViewModel.operationState.collectAsState()
    val roleOpState by roleViewModel.operationState.collectAsState()
    val areaOpState by areaViewModel.operationState.collectAsState()
    val projectOpState by projectViewModel.operationState.collectAsState()

    var showUserDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf<Role?>(null) }

    var showAreaDialog by remember { mutableStateOf(false) }
    var selectedArea by remember { mutableStateOf<Area?>(null) }

    var showProjectDialog by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }

    // Initial data load
    LaunchedEffect(Unit) {
        userViewModel.fetchUsers(token)
        roleViewModel.fetchRoles()
        areaViewModel.fetchAreas()
        projectViewModel.fetchProjects()
    }

    // Handle Operation States
    LaunchedEffect(userOpState, roleOpState, areaOpState, projectOpState) {
        val states = listOf(userOpState, roleOpState, areaOpState, projectOpState)
        states.forEach { state ->
            if (state is OperationState.Success) {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                showUserDialog = false
                showRoleDialog = false
                showAreaDialog = false
                showProjectDialog = false
                userViewModel.resetOperationState()
                roleViewModel.resetOperationState()
                areaViewModel.resetOperationState()
                projectViewModel.resetOperationState()
            } else if (state is OperationState.Error) {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Administración IIAP", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            when (selectedTab) {
                                0 -> { selectedUser = null; showUserDialog = true }
                                1 -> { selectedRole = null; showRoleDialog = true }
                                2 -> { selectedArea = null; showAreaDialog = true }
                                3 -> { selectedProject = null; showProjectDialog = true }
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir")
                        }
                    }
                )
                ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 16.dp) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> UserTabContent(
                    state = userListState,
                    onEdit = { selectedUser = it; showUserDialog = true },
                    onDelete = { it.id?.let { id -> userViewModel.deleteUser(token, id) } }
                )
                1 -> RoleTabContent(
                    state = roleListState,
                    onEdit = { selectedRole = it; showRoleDialog = true },
                    onDelete = { it.id?.let { id -> roleViewModel.deleteRole(token, id) } }
                )
                2 -> AreaTabContent(
                    state = areaListState,
                    onEdit = { selectedArea = it; showAreaDialog = true },
                    onDelete = { it.id?.let { id -> areaViewModel.deleteArea(token, id) } }
                )
                3 -> ProjectTabContent(
                    state = projectListState,
                    onEdit = { selectedProject = it; showProjectDialog = true },
                    onDelete = { it.id?.let { id -> projectViewModel.deleteProject(token, id) } }
                )
            }
        }
    }

    // Dialogs
    if (showUserDialog) {
        val roles = (roleListState as? RoleListState.Success)?.roles ?: emptyList()
        UserActionDialog(
            user = selectedUser,
            roles = roles,
            onDismiss = { showUserDialog = false },
            onConfirm = { name, email, roleId ->
                if (selectedUser == null) {
                    userViewModel.createUser(token, User(auth0Id = "manual-${System.currentTimeMillis()}", name = name, email = email, role_id = roleId))
                } else {
                    userViewModel.updateUser(token, selectedUser!!.id!!, selectedUser!!.copy(name = name, email = email, role_id = roleId))
                }
            }
        )
    }

    if (showRoleDialog) {
        RoleActionDialog(
            role = selectedRole,
            onDismiss = { showRoleDialog = false },
            onConfirm = { name, desc ->
                if (selectedRole == null) {
                    roleViewModel.createRole(token, Role(name = name, description = desc))
                } else {
                    roleViewModel.updateRole(token, selectedRole!!.id!!, selectedRole!!.copy(name = name, description = desc))
                }
            }
        )
    }

    if (showAreaDialog) {
        AreaActionDialog(
            area = selectedArea,
            onDismiss = { showAreaDialog = false },
            onConfirm = { description, logo, userId ->
                val area = Area(description = description, logo = logo, user_id = userId)
                if (selectedArea == null) {
                    areaViewModel.createArea(token, area)
                } else {
                    areaViewModel.updateArea(token, selectedArea!!.id!!, area)
                }
            }
        )
    }

    if (showProjectDialog) {
        ProjectActionDialog(
            project = selectedProject,
            onDismiss = { showProjectDialog = false },
            onConfirm = { name, desc, start, end ->
                val project = Project(name = name, description = desc, startDate = start, endDate = end)
                if (selectedProject == null) {
                    projectViewModel.createProject(token, project)
                } else {
                    projectViewModel.updateProject(token, selectedProject!!.id!!, project)
                }
            }
        )
    }
}

/* -------------------- TAB CONTENTS -------------------- */

@Composable
fun UserTabContent(state: UserListState, onEdit: (User) -> Unit, onDelete: (User) -> Unit) {
    when (state) {
        is UserListState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        is UserListState.Success -> UserList(state.users, onEdit, onDelete)
        is UserListState.Error -> Box(Modifier.fillMaxSize()) { Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center)) }
        else -> {}
    }
}

@Composable
fun RoleTabContent(state: RoleListState, onEdit: (Role) -> Unit, onDelete: (Role) -> Unit) {
    when (state) {
        is RoleListState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        is RoleListState.Success -> RoleList(state.roles, onEdit, onDelete)
        is RoleListState.Error -> Box(Modifier.fillMaxSize()) { Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center)) }
        else -> {}
    }
}

@Composable
fun AreaTabContent(state: AreaListState, onEdit: (Area) -> Unit, onDelete: (Area) -> Unit) {
    when (state) {
        is AreaListState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        is AreaListState.Success -> AreaList(state.areas, onEdit, onDelete)
        is AreaListState.Error -> Box(Modifier.fillMaxSize()) { Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center)) }
        else -> {}
    }
}

@Composable
fun ProjectTabContent(state: ProjectListState, onEdit: (Project) -> Unit, onDelete: (Project) -> Unit) {
    when (state) {
        is ProjectListState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        is ProjectListState.Success -> ProjectList(state.projects, onEdit, onDelete)
        is ProjectListState.Error -> Box(Modifier.fillMaxSize()) { Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center)) }
        else -> {}
    }
}

/* -------------------- LISTS & CARDS -------------------- */

@Composable
fun UserList(users: List<User>, onEdit: (User) -> Unit, onDelete: (User) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(users) { user ->
            AdminCard(title = user.name, subtitle = user.email, extra = "Rol ID: ${user.role_id}", onEdit = { onEdit(user) }, onDelete = { onDelete(user) })
        }
    }
}

@Composable
fun RoleList(roles: List<Role>, onEdit: (Role) -> Unit, onDelete: (Role) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(roles) { role ->
            AdminCard(title = role.name, subtitle = role.description ?: "Sin descripción", extra = "ID: ${role.id}", onEdit = { onEdit(role) }, onDelete = { onDelete(role) })
        }
    }
}

@Composable
fun AreaList(areas: List<Area>, onEdit: (Area) -> Unit, onDelete: (Area) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(areas) { area ->
            AdminCard(title = area.description, subtitle = "Logo: ${area.logo ?: "N/A"}", extra = "ID: ${area.id} | User: ${area.user_id}", onEdit = { onEdit(area) }, onDelete = { onDelete(area) })
        }
    }
}

@Composable
fun ProjectList(projects: List<Project>, onEdit: (Project) -> Unit, onDelete: (Project) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(projects) { project ->
            AdminCard(title = project.name ?: "Sin nombre", subtitle = project.description ?: "Sin descripción", extra = "Del ${project.startDate.take(10)} al ${project.endDate.take(10)}", onEdit = { onEdit(project) }, onDelete = { onDelete(project) })
        }
    }
}

@Composable
fun AdminCard(title: String, subtitle: String, extra: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
                Text(extra, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
        }
    }
}

/* -------------------- DIALOGS -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserActionDialog(user: User?, roles: List<Role>, onDismiss: () -> Unit, onConfirm: (String, String, Int) -> Unit) {
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var selectedRoleId by remember { mutableIntStateOf(user?.role_id ?: 1) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (user == null) "Nuevo Usuario" else "Editar Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                
                // Role Selector
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = roles.find { it.id == selectedRoleId }?.name ?: "Seleccionar Rol",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.name) },
                                onClick = { selectedRoleId = role.id ?: 1; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(name, email, selectedRoleId) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun RoleActionDialog(role: Role?, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf(role?.name ?: "") }
    var desc by remember { mutableStateOf(role?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (role == null) "Nuevo Rol" else "Editar Rol") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del Rol") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(name, desc) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun AreaActionDialog(area: Area?, onDismiss: () -> Unit, onConfirm: (String, String, Int) -> Unit) {
    var description by remember { mutableStateOf(area?.description ?: "") }
    var logo by remember { mutableStateOf(area?.logo ?: "") }
    var userId by remember { mutableStateOf(area?.user_id?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (area == null) "Nueva Área" else "Editar Área") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                OutlinedTextField(value = logo, onValueChange = { logo = it }, label = { Text("URL del Logo") })
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("ID de Usuario Responsable") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(description, logo, userId.toIntOrNull() ?: 0) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun ProjectActionDialog(project: Project?, onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(project?.name ?: "") }
    var desc by remember { mutableStateOf(project?.description ?: "") }
    var startDate by remember { mutableStateOf(project?.startDate?.take(10) ?: "") }
    var endDate by remember { mutableStateOf(project?.endDate?.take(10) ?: "") }

    fun showDatePicker(currentDate: String, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (project == null) "Nuevo Proyecto" else "Editar Proyecto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del Proyecto") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") })
                
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {},
                    label = { Text("Fecha de Inicio") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker(startDate) { startDate = it } }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    }
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = {},
                    label = { Text("Fecha de Fin") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker(endDate) { endDate = it } }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    }
                )
            }
        },
        confirmButton = { Button(onClick = { onConfirm(name, desc, startDate, endDate) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
