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
    // LA MAGIA DE FLOW - La lista se actualiza sola
    // ══════════════════════════════════════════════

    // 1. Usamos el Flow de LocalDatabase y lo convertimos en un StateFlow
    // que la UI puede consumir de forma segura.
    val insects: StateFlow<List<Insect>> = localDb.getAllInsects()
        .stateIn(
            scope = viewModelScope, // Se ata al ciclo de vida del ViewModel
            started = SharingStarted.WhileSubscribed(5000L), // Inicia cuando hay un observador
            initialValue = emptyList() // Valor inicial mientras carga
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

            _isLoading.value = true

            val insect = Insect(name = name, imgLocation = imgLocation)
            val success = localDb.addInsect(insect)

            if (success) {
                _message.emit("Insecto agregado correctamente")
            } else {
                _message.emit("Error al agregar el insecto")
            }
            _isLoading.value = false
        }
    }

    /**
     * Eliminar un insecto
     */
    fun deleteInsect(insect: Insect) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = localDb.deleteInsect(insect)

            if (success) {
                _message.emit("${insect.name} eliminado")
            } else {
                _message.emit("Error al eliminar")
            }
            _isLoading.value = false
        }
    }
}