package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.Study
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class StudyListState {
    object Idle : StudyListState()
    object Loading : StudyListState()
    data class Success(val studies: List<Study>) : StudyListState()
    data class Error(val message: String) : StudyListState()
}

class StudyViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    
    private val _studies = MutableStateFlow<List<Study>>(emptyList())
    val studies = _studies.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    fun fetchUserStudies(userUid: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("estudios")
                    .whereEqualTo("userUid", userUid)
                    .get().await()
                val studiesList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Study::class.java)?.copy(id = doc.id)
                }
                _studies.value = studiesList
            } catch (e: Exception) { }
        }
    }

    fun createStudy(userUid: String, study: Study) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val studyData = hashMapOf(
                    "userUid" to userUid,
                    "degree" to study.degree,
                    "institution" to study.institution,
                    "fieldOfStudy" to study.fieldOfStudy,
                    "startDate" to study.startDate
                )
                db.collection("estudios").add(studyData).await()
                _operationState.value = OperationState.Success("Estudio agregado")
                fetchUserStudies(userUid)
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun deleteStudy(studyId: String, userUid: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                db.collection("estudios").document(studyId).delete().await()
                _operationState.value = OperationState.Success("Estudio eliminado")
                fetchUserStudies(userUid)
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Error al eliminar")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
