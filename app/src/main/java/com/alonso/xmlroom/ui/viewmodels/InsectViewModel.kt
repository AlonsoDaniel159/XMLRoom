package com.alonso.xmlroom.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonso.xmlroom.data.local.entity.Insect
import com.alonso.xmlroom.data.repository.InsectRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.alonso.xmlroom.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * ViewModel para manejar la lógica de insectos
 * Sobrevive a rotaciones de pantalla
 */
class InsectViewModel(
    private val repository: InsectRepository,
    userId: Long
) : ViewModel() {
    private val _filterType = MutableStateFlow(FilterType.USER)

    private val _message = MutableSharedFlow<String>(
        replay = 0,                // No repetir eventos pasados
        extraBufferCapacity = 1,   // Buffer de 1 evento
        onBufferOverflow = BufferOverflow.DROP_OLDEST  // Si está lleno, descartar el viejo
    )
    val message: SharedFlow<String> = _message.asSharedFlow()

    //Enum para los tipos de filtro
    enum class FilterType {
        ALL, USER
    }

    // Flujo privado para todos los insectos, cacheado permanentemente mientras el ViewModel viva.
    val allInsects: StateFlow<UiState<List<Insect>>> = repository.getAllInsects()
        .stateIn(
            scope = viewModelScope,
            // SharingStarted.Eagerly inicia inmediatamente y nunca se detiene.
            // Es bueno para datos que quieres tener siempre listos.
            started = SharingStarted.Eagerly,
            initialValue = UiState.Loading
        )

    // Flujo privado para los insectos del usuario, también cacheado.
    val userInsects: StateFlow<UiState<List<Insect>>> = repository.getInsectsByUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState.Loading
        )

    // Este es el StateFlow que tu UI observará.
    @OptIn(ExperimentalCoroutinesApi::class)
    val insects: StateFlow<UiState<List<Insect>>> = _filterType
        .flatMapLatest { filterType ->
            when (filterType) {
                FilterType.ALL -> allInsects
                FilterType.USER -> userInsects
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = allInsects.value
        )

    // La función para cambiar el filtro no cambia.
    fun setFilter(filterType: FilterType) {
        _filterType.value = filterType
    }

    fun getAllInsects() {
        _filterType.value = FilterType.ALL
    }

    fun getInsectsByUser() {
        _filterType.value = FilterType.USER
    }

    /**
     * Agregar un nuevo insecto
     */
    fun addInsect(name: String, imgLocation: String = "", userId: Long) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _message.emit("El nombre no puede estar vacío")
                return@launch
            }

            val insect = Insect(name = name, imgLocation = imgLocation, userId = userId)

            // Intentar agregar (usa kotlin.Result)
            repository.addInsect(insect)
                .onSuccess { _message.emit("Insecto agregado") }
                .onFailure { error ->
                    Log.e("approom", "Error: ${error.message}")
                    _message.emit("Error:  ${error.message}")
                }
        }
    }

    /**
     * Eliminar un insecto
     */
    fun deleteInsect(insect: Insect) {
        viewModelScope.launch {
            repository.deleteInsect(insect)
                .onSuccess {
                    _message.emit("🗑${insect.name} eliminado")
                }
                .onFailure { error ->
                    Log.e("approom", "Error: ${error.message}")
                    _message.emit("Error al eliminar: ${error.message}")
                }
        }
    }
}