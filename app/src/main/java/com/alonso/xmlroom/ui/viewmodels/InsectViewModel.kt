package com.alonso.xmlroom.ui.viewmodels

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

/**
 * ViewModel para manejar la lógica de insectos
 * Sobrevive a rotaciones de pantalla
 */
class InsectViewModel(private val repository: InsectRepository) : ViewModel() {

    private val _message = MutableSharedFlow<String>(
        replay = 0,                // No repetir eventos pasados
        extraBufferCapacity = 1,   // Buffer de 1 evento
        onBufferOverflow = BufferOverflow.DROP_OLDEST  // Si está lleno, descartar el viejo
    )
    val message: SharedFlow<String> = _message.asSharedFlow()

    // ══════════════════════════════════════════════
    // StateFlow con UiState para lista de insectos
    // ══════════════════════════════════════════════
    val insects: StateFlow<UiState<List<Insect>>> = repository.getAllInsects()
        .stateIn(
            scope = viewModelScope, // Se ata al ciclo de vida del ViewModel
            started = SharingStarted.WhileSubscribed(5000L), // Inicia cuando hay un observador
            initialValue = UiState.Loading // Estado inicial Loading
        )

    /**
     * Agregar un nuevo insecto
     */
    fun addInsect(name: String, imgLocation: String = "", userId: Long) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _message.emit("El nombre no puede estar vacío")
                return@launch
            }

            val insect = Insect(name = name, imgLocation = imgLocation)

            // Intentar agregar (usa kotlin.Result)
            repository.addInsect(insect, userId)
                .onSuccess { _message.emit("Insecto agregado") }
                .onFailure { error -> _message.emit("Error:  ${error.message}") }
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
                    _message.emit("Error al eliminar: ${error.message}")
                }
        }
    }

    fun loadInsectsForUser(currentUserId: Long) {
        viewModelScope.launch {
            _message.emit("Cargando insectos del usuario $currentUserId")
        }
    }
}