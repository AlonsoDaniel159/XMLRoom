package com.alonso.xmlroom.ui.events

// en un nuevo archivo, ej: ui/events/RegisterEvent.kt
sealed class RegisterEvent {
    // Evento para mostrar un mensaje de éxito
    data class Success(val message: String) : RegisterEvent()
    // Evento para mostrar un mensaje de error
    data class Error(val message: String) : RegisterEvent()
    // Podrías añadir más eventos aquí, como NavigateToLogin
    data object NavigateToHome : RegisterEvent()
}
