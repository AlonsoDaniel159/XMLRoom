package com.alonso.xmlroom.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alonso.xmlroom.room.entity.User
import com.alonso.xmlroom.room.entity.UserAuth

import com.alonso.xmlroom.room.Constants.E_USERS
import com.alonso.xmlroom.room.Constants.P_USER_ID_PK
import com.alonso.xmlroom.room.Constants.P_FIRST_NAME
import com.alonso.xmlroom.room.Constants.P_EMAIL
import com.alonso.xmlroom.room.Constants.P_PIN

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
    @Query("""
        SELECT $P_USER_ID_PK, $P_FIRST_NAME
        FROM $E_USERS 
        WHERE $P_EMAIL = :email 
        AND $P_PIN = :pin 
        LIMIT 1
    """)
    suspend fun login(email: String, pin: Int): UserAuth?

    // 🔍 Buscar usuario por email
    @Query("""
        SELECT $P_USER_ID_PK, $P_FIRST_NAME
        FROM $E_USERS 
        WHERE $P_EMAIL = :email 
        LIMIT 1
    """)
    suspend fun findUserByEmail(email: String): UserAuth?

    // 🔍 Buscar usuario por ID
    @Query("SELECT * FROM ${Constants.E_USERS} WHERE $P_USER_ID_PK = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    // 📋 Obtener todos los usuarios
    @Query("SELECT * FROM $E_USERS")
    suspend fun getAllUsers(): List<User>
}