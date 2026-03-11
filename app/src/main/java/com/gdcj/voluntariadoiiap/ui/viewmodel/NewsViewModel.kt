package com.gdcj.voluntariadoiiap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdcj.voluntariadoiiap.data.model.News
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NewsState {
    object Loading : NewsState()
    data class Success(val news: List<News>) : NewsState()
    data class Error(val message: String) : NewsState()
}

class NewsViewModel : ViewModel() {
    private val _newsState = MutableStateFlow<NewsState>(NewsState.Loading)
    val newsState = _newsState.asStateFlow()

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _newsState.value = NewsState.Loading
            try {
                // Simulación de delay de red
                delay(1500)
                
                val dummyNews = listOf(
                    News(
                        1, 
                        "IIAP lanza nueva campaña de reforestación", 
                        "El Instituto de Investigaciones de la Amazonía Peruana (IIAP) inicia un ambicioso proyecto para restaurar 500 hectáreas...",
                        "https://iiap.gob.pe/Archivos/Noticias/Noticia_202401.jpg",
                        "Hace 2 horas",
                        "https://iiap.gob.pe"
                    ),
                    News(
                        2, 
                        "Descubren nueva especie de pez en el Nanay", 
                        "Investigadores del IIAP han identificado una nueva especie del género Corydoras durante expediciones científicas...",
                        "https://iiap.gob.pe/Archivos/Noticias/Noticia_202402.jpg",
                        "Hoy, 10:00 AM",
                        "https://iiap.gob.pe"
                    ),
                    News(
                        3, 
                        "Taller de Bio-negocios para comunidades", 
                        "Más de 30 líderes comunales participaron en la capacitación sobre manejo sostenible de frutos amazónicos...",
                        "https://iiap.gob.pe/Archivos/Noticias/Noticia_202403.jpg",
                        "Ayer",
                        "https://iiap.gob.pe"
                    )
                )
                _newsState.value = NewsState.Success(dummyNews)
            } catch (e: Exception) {
                _newsState.value = NewsState.Error("No se pudieron cargar las noticias")
            }
        }
    }
}
