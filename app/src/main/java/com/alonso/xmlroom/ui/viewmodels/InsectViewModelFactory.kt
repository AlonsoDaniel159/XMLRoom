package com.alonso.xmlroom.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alonso.xmlroom.data.repository.InsectRepository

class InsectViewModelFactory(private val repository: InsectRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InsectViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}