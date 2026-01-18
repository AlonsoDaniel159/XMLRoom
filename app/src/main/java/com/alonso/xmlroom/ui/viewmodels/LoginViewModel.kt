package com.alonso.xmlroom.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonso.xmlroom.data.local.entity.User
import com.alonso.xmlroom.data.preferences.UserPreferences
import com.alonso.xmlroom.data.repository.UserRepository
import com.alonso.xmlroom.ui.events.RegisterEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userPreferences: UserPreferences,
    private val repository: UserRepository): ViewModel() {

    private val _event = MutableSharedFlow<RegisterEvent>()
    val event: SharedFlow<RegisterEvent> = _event.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
                .onSuccess { user ->
                    userPreferences.saveUserId(user.id)
                    _event.emit(RegisterEvent.NavigateToHome)
                }
                .onFailure { error ->
                    Log.e("approom","Error: ${error.message}")
                    _event.emit(RegisterEvent.Error("Error: ${error.message}"))
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
            _event.emit(RegisterEvent.Success("Sesión cerrada"))
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            repository.registerUser(user)
                .onSuccess { user ->
                    userPreferences.saveUserId(user.id)
                    _event.emit(RegisterEvent.NavigateToHome)
                }
                .onFailure { error ->
                    Log.e("approom","Error: ${error.message}")
                    _event.emit(RegisterEvent.Error("Error: ${error.message}"))
                }
        }
    }
}