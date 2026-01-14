package com.alonso.xmlroom.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonso.xmlroom.room.LocalDatabase
import com.alonso.xmlroom.room.entity.Insect
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.alonso.xmlroom.utils.UiState

/**
 * ViewModel para manejar la lógica de insectos
 * Sobrevive a rotaciones de pantalla
 */
class InsectViewModel : ViewModel() {

    private val localDb = LocalDatabase()

    private val _message = MutableSharedFlow<String>(
        replay = 0,                // No repetir eventos pasados
        extraBufferCapacity = 1,   // Buffer de 1 evento
        onBufferOverflow = BufferOverflow.DROP_OLDEST  // Si está lleno, descartar el viejo
    )
    val message: SharedFlow<String> = _message.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ══════════════════════════════════════════════
    // StateFlow con UiState para lista de insectos
    // ══════════════════════════════════════════════
    val insects: StateFlow<UiState<List<Insect>>> = localDb.getAllInsects()
        .stateIn(
            scope = viewModelScope, // Se ata al ciclo de vida del ViewModel
            started = SharingStarted.WhileSubscribed(5000L), // Inicia cuando hay un observador
            initialValue = UiState.Loading // Estado inicial Loading
        )

    /**
     * Agregar un nuevo insecto
     */
    fun addInsect(name: String, imgLocation: String = "") {
        viewModelScope.launch {
            if (name.isBlank()) {
                _message.emit("El nombre no puede estar vacío")
                return@launch
            }

            val insect = Insect(name = name, imgLocation = imgLocation)

            // Intentar agregar (usa kotlin.Result)
            localDb.addInsect(insect)
                .onSuccess { id ->
                    _message.emit("Insecto agregado")
                }
                .onFailure { error ->
                    _message.emit("Error:  ${error.message}")
                }
        }
    }

    /**
     * Eliminar un insecto
     */
    fun deleteInsect(insect: Insect) {
        viewModelScope.launch {
            localDb.deleteInsect(insect)
                .onSuccess {
                    _message.emit("🗑${insect.name} eliminado")
                }
                .onFailure { error ->
                    _message.emit("Error al eliminar: ${error.message}")
                }
        }
    }
}