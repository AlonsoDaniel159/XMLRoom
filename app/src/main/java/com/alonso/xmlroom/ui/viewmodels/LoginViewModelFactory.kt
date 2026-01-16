package com.alonso.xmlroom.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alonso.xmlroom.room.LocalDatabase
import com.alonso.xmlroom.utils.UserPreferences

class LoginViewModelFactory(
    private val userPreferences: UserPreferences,
    private val localDb: LocalDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userPreferences, localDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
