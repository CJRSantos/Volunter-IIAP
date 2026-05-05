package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProjectListState {
    object Idle : ProjectListState()
    object Loading : ProjectListState()
    data class Success(val projects: List<Project>) : ProjectListState()
    data class Error(val message: String) : ProjectListState()
}

class ProjectViewModel : ViewModel() {
    private val _projectListState = MutableStateFlow<ProjectListState>(ProjectListState.Idle)
    val projectListState = _projectListState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchProjects() {
        viewModelScope.launch {
            _projectListState.value = ProjectListState.Loading
            val mockProjects = listOf(
                Project(id = 1, name = "Monitoreo de Delfines Rosados", description = "Seguimiento de poblaciones en el río Amazonas.", startDate = "2024-01-01", endDate = "2024-12-31"),
                Project(id = 2, name = "Reforestación de Bosques Inundables", description = "Plantación de especies nativas en zonas de ribera.", startDate = "2024-02-01", endDate = "2024-11-30"),
                Project(id = 3, name = "Calidad de Agua en la Amazonía", description = "Análisis fisicoquímico de principales afluentes.", startDate = "2024-03-01", endDate = "2024-10-31"),
                Project(id = 4, name = "Estudio de Peces Ornamentales", description = "Investigación sobre reproducción en cautiverio.", startDate = "2024-04-01", endDate = "2024-09-30")
            )
            _projectListState.value = ProjectListState.Success(mockProjects)
        }
    }

    fun createProject(project: Project) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            _operationState.value = OperationState.Success("Proyecto creado localmente")
            fetchProjects()
        }
    }

    fun updateProject(id: Int, project: Project) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            _operationState.value = OperationState.Success("Proyecto actualizado localmente")
            fetchProjects()
        }
    }

    fun deleteProject(id: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            _operationState.value = OperationState.Success("Proyecto eliminado localmente")
            fetchProjects()
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
