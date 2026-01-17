package com.alonso.xmlroom.data.local.dao

import androidx.room.Dao
import androidx.room.Transaction

@Dao
interface TransactionDao: InsectDao, UserDao {

    /**
     * Elimina un usuario Y todos sus insectos en una sola transacción
     * Si algo falla, se revierte TODO (atomicidad)
     */
    @Transaction
    suspend fun deleteUserAndData(userId: Long) {
        // 1. Obtener el usuario
        getUserById(userId)?.let { user ->
            deleteInsectsByUserId(user.id)
            deleteUser(user)
        }
        // Si cualquier paso falla, Room revierte TODO automáticamente
    }
}