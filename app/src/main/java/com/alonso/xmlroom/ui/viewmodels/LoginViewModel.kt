package com.alonso.xmlroom.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonso.xmlroom.room.LocalDatabase
import com.alonso.xmlroom.utils.UserPreferences
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userPreferences: UserPreferences,
    private val localDb: LocalDatabase): ViewModel() {

    private val _navigateToHome  = MutableSharedFlow<Unit>(
        replay = 0,                // No repetir eventos pasados
        extraBufferCapacity = 1,   // Buffer de 1 evento
        onBufferOverflow = BufferOverflow.DROP_OLDEST  // Si está lleno, descartar el viejo
    )
    val navigateToHome = _navigateToHome.asSharedFlow()

    private val _message = MutableSharedFlow<String>(
        replay = 0,                // No repetir eventos pasados
        extraBufferCapacity = 1,   // Buffer de 1 evento
        onBufferOverflow = BufferOverflow.DROP_OLDEST  // Si está lleno, descartar el viejo
    )
    val message: SharedFlow<String> = _message.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            localDb.login(email, password)
                .onSuccess { user ->
                    userPreferences.saveUserId(user.id)
                    _navigateToHome.emit(Unit)
                }
                .onFailure { error ->
                    _message.emit("Error: ${error.message}")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
            _message.emit("Sesión cerrada")
        }
    }
}