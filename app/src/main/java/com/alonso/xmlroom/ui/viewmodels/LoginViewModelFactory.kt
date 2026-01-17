package com.alonso.xmlroom.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alonso.xmlroom.data.preferences.UserPreferences
import com.alonso.xmlroom.data.repository.UserRepository

class LoginViewModelFactory(
    private val userPreferences: UserPreferences,
    private val repository: UserRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userPreferences, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
