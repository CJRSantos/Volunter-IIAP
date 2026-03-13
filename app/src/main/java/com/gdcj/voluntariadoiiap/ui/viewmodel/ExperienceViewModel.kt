package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Experience
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExperienceViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    
    private val _experiences = MutableStateFlow<List<Experience>>(emptyList())
    val experiences = _experiences.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchUserExperiences(userUid: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("experiencias")
                    .whereEqualTo("userUid", userUid)
                    .get().await()
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Experience::class.java)?.copy(id = doc.id)
                }
                _experiences.value = list
            } catch (e: Exception) { }
        }
    }

    fun createExperience(userUid: String, experience: Experience) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val expData = hashMapOf(
                    "userUid" to userUid,
                    "position" to experience.position,
                    "company" to experience.company,
                    "startDate" to experience.startDate,
                    "description" to experience.description
                )
                db.collection("experiencias").add(expData).await()
                _operationState.value = OperationState.Success("Experiencia agregada")
                fetchUserExperiences(userUid)
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun updateExperience(userUid: String, experienceId: String, experience: Experience) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val expData = hashMapOf(
                    "userUid" to userUid,
                    "position" to experience.position,
                    "company" to experience.company,
                    "startDate" to experience.startDate,
                    "description" to experience.description
                )
                db.collection("experiencias").document(experienceId).set(expData).await()
                _operationState.value = OperationState.Success("Experiencia actualizada")
                fetchUserExperiences(userUid)
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteExperience(experienceId: String, userUid: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                db.collection("experiencias").document(experienceId).delete().await()
                _operationState.value = OperationState.Success("Experiencia eliminada")
                fetchUserExperiences(userUid)
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error al eliminar")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
