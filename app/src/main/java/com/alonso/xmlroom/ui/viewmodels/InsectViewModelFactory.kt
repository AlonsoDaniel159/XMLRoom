package com.alonso.xmlroom.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alonso.xmlroom.data.repository.InsectRepository

class InsectViewModelFactory(private val repository: InsectRepository, private val userId: Long): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InsectViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}