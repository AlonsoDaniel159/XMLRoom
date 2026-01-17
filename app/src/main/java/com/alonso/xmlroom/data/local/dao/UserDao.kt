package com.alonso.xmlroom.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alonso.xmlroom.data.local.entity.User

import com.alonso.xmlroom.data.local.Constants.E_USERS
import com.alonso.xmlroom.data.local.Constants.P_USER_ID_PK
import com.alonso.xmlroom.data.local.Constants.P_EMAIL

@Dao
interface UserDao {

    // ➕ Registrar nuevo usuario
    @Insert
    suspend fun addUser(user: User): Long

    // ✏️ Actualizar usuario
    @Update
    suspend fun updateUser(user: User): Int

    // ❌ Eliminar usuario
    @Delete
    suspend fun deleteUser(user: User): Int

    // 🔐 Login - Retorna UserAuth si las credenciales son correctas
    @Query(" SELECT * FROM $E_USERS WHERE $P_EMAIL = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // 🔍 Buscar usuario por ID
    @Query("SELECT * FROM ${E_USERS} WHERE $P_USER_ID_PK = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    // 📋 Obtener todos los usuarios
    @Query("SELECT * FROM $E_USERS")
    suspend fun getAllUsers(): List<User>
}